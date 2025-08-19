package com.green.screen.banking.algebras

import cats.effect.*
import com.green.screen.banking.BankingPostgresSuite
import com.green.screen.generators.companies.*
import com.green.screen.generators.transactions.*
import weaver.*
import weaver.scalacheck.*
import com.green.screen.generators.users.*

object OpenAPITransactionsSuite extends BankingPostgresSuite:
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
