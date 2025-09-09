package com.green.screen.banking.algebras

import cats.effect.*
import com.green.screen.banking.BankingPostgresSuite
import com.green.screen.banking.generators.companies.*
import com.green.screen.banking.generators.transactions.*
import weaver.*
import weaver.scalacheck.*
import com.green.screen.banking.generators.users.*

object OpenBankingTransactionsSuite extends BankingPostgresSuite:
  test("Should be able to create and retrieve open_banking_transactions") { postgres =>
    val companiesAlgebra: Companies[IO]                  = Companies.make[IO](postgres)
    val usersAlgebra: Users[IO]                          = Users.make[IO](postgres)
    val transactionsAlgebra: OpenBankingTransactions[IO] = OpenBankingTransactions.make[IO](postgres)

    val gen = for {
      company     <- companyGen
      user        <- userGen
      transaction <- OpenBankingTransactionGen(company.uuid, user.uuid)
    } yield (company, transaction, user)

    forall(gen) { case (company, transaction, user) =>
      for {
        _            <- companiesAlgebra.createCompany(company)
        _            <- usersAlgebra.createUser(user)
        _            <- transactionsAlgebra.createTransaction(transaction)
        transactions <- transactionsAlgebra.getTransactions(company.uuid)
      } yield expect.same(transactions, List(transaction))
    }
  }
end OpenBankingTransactionsSuite
