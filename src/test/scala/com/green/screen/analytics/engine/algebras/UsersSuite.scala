package com.green.screen.analytics.engine.algebras

import cats.effect.*
import com.green.screen.analytics.engine.generators.companies.companyGen
import com.green.screen.analytics.engine.generators.transactions.openAPITransactionGen
import com.green.screen.analytics.engine.generators.users.*
import com.green.screen.analytics.engine.{ PostgresSuite, ResourceSuite }
import com.green.screen.analytics.engine.generators.*
import cats.syntax.all.*
import com.green.screen.analytics.engine.domain.UserScore
import scala.math.BigDecimal.RoundingMode
import eu.timepit.refined.types.all.*

object UsersSuite extends PostgresSuite:
  test("Should be able to insert and get a user") { postgres =>
    val usersAlgebra: Users[IO] = Users.make[IO](postgres)

    forall(userGen) { user =>
      for {
        _      <- usersAlgebra.createUser(user)
        result <- usersAlgebra.getUser(user.uuid)
      } yield expect.same(result, Some(user))
    }
  }

  test("Should be able to get the average emission scores for all of a users transactions") { postgres =>
    val companiesAlgebra: Companies[IO]                     = Companies.make[IO](postgres)
    val usersAlgebra: Users[IO]                             = Users.make[IO](postgres)
    val openAPITransactionsAlgebra: OpenAPITransactions[IO] = OpenAPITransactions.make[IO](postgres)

    val gen = for {
      user         <- userGen
      companies    <- nelGen(companyGen)
      transactions <- sequenceListGen(companies.toList)(company => openAPITransactionGen(company.uuid, user.uuid))
    } yield (companies, transactions, user)

    forall(gen) { case (companies, transactions, user) =>
      for {
        _     <- companies.traverse(companiesAlgebra.createCompany)
        _     <- usersAlgebra.createUser(user)
        _     <- transactions.traverse(openAPITransactionsAlgebra.createTransaction)
        score <- usersAlgebra.getScore(user.uuid)
        expectedScore = BigDecimal
          .decimal(companies.map(company => company.co2Emissions.value).toList.sum / companies.length)
          .setScale(2, RoundingMode.HALF_UP)
      } yield expect.same(
        BigDecimal.decimal(score.value.value),
        expectedScore
      )
    }
  }

  test("Should return 0 if there is no transaction data for the user") { postgres =>
    val usersAlgebra: Users[IO] = Users.make[IO](postgres)

    forall(userGen) { user =>
      for {
        _     <- usersAlgebra.createUser(user)
        score <- usersAlgebra.getScore(user.uuid)
        expectedScore = UserScore(NonNegDouble.unsafeFrom(0))
      } yield expect.same(
        score,
        expectedScore
      )
    }
  }
end UsersSuite
