package com.green.screen.analytics.engine.algebras

import cats.data.NonEmptyList
import cats.effect.*
import com.green.screen.analytics.engine.domain.companies.Company.*
import com.green.screen.analytics.engine.generators.*
import com.green.screen.analytics.engine.generators.companies.*
import com.green.screen.analytics.engine.generators.transactions.*
import com.green.screen.analytics.engine.{PostgresSuite, ResourceSuite}
import weaver.*
import weaver.scalacheck.*

object UserTransactionsSuite extends PostgresSuite:
  test("Should be able to create and retrieve transactions") { postgres =>
    val companiesAlgebra: Companies[IO] = Companies.make[IO](postgres)
    val userTransactionsAlgebra: UserTransactions[IO] = UserTransactions.make[IO](postgres)

    val gen = for {
      company <- companyGen
      transaction <- transactionGen(company.uuid)
    } yield (company, transaction)

    forall(gen) { case (company, transaction) =>

      for {
        _ <- companiesAlgebra.createCompanies(NonEmptyList.one(company))
        _ <- userTransactionsAlgebra.createTransaction(transaction)
        transactions <- userTransactionsAlgebra.getTransactions(company.uuid)
      } yield expect.same(transactions, List(transaction))
    }
  }
end UserTransactionsSuite
