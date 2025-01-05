package com.green.screen.analytics.engine.algebras

import cats.effect.*
import com.green.screen.analytics.engine.domain.Company.*
import com.green.screen.analytics.engine.generators.*
import com.green.screen.analytics.engine.generators.companies.*
import com.green.screen.analytics.engine.generators.transactions.*
import com.green.screen.analytics.engine.{ PostgresSuite, ResourceSuite }
import weaver.*
import weaver.scalacheck.*
import com.green.screen.analytics.engine.generators.users.*

object OpenAPITransactionsSuite extends PostgresSuite:
  test("Should be able to create and retrieve transactions") { postgres =>
    val companiesAlgebra: Companies[IO]                     = Companies.make[IO](postgres)
    val usersAlgebra: Users[IO]                             = Users.make[IO](postgres)
    val openAPITransactionsAlgebra: OpenAPITransactions[IO] = OpenAPITransactions.make[IO](postgres)

    val gen = for {
      company     <- companyGen
      user        <- userGen
      transaction <- openAPITransactionGen(company.uuid, user.uuid)
    } yield (company, transaction, user)

    forall(gen) { case (company, transaction, user) =>
      for {
        _            <- companiesAlgebra.createCompany(company)
        _            <- usersAlgebra.createUser(user)
        _            <- openAPITransactionsAlgebra.createTransaction(transaction)
        transactions <- openAPITransactionsAlgebra.getTransactions(company.uuid)
      } yield expect.same(transactions, List(transaction))
    }
  }
end OpenAPITransactionsSuite
