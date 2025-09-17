package com.green.screen.banking.programs

import cats.Id
import munit.{FunSuite, ScalaCheckSuite}
import org.scalacheck.Prop.forAll
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.noop.NoOpLogger
import com.green.screen.banking.domain.companies.{CompanyName, CompanyUuid}
import com.green.screen.banking.generators.companies.companyUuidGen
import com.green.screen.banking.generators.transactions.createTransactionRequestGen
import com.green.screen.banking.programs.ProcessTransaction.CompanyNotFound
import com.green.screen.common.GivenInstances.given
import cats.syntax.all.*
import com.green.screen.banking.algebras.{TestCompanies, TestOpenBankingTransactions}
import com.green.screen.banking.domain.transactions.OpenBankingTransaction

class ProcessTransactionSuite extends FunSuite with ScalaCheckSuite {

  given Logger[Id] = NoOpLogger[Id]

  test("Should fail if no company is found for the given transaction") {
    forAll(createTransactionRequestGen) { createTransactionRequest =>

        val companies = new TestCompanies[Id] {
          override def getCompanyUuidByName(transactionEntity: CompanyName): Id[Option[CompanyUuid]] = None
        }

        val processTransaction = new ProcessTransaction[Id](companies, new TestOpenBankingTransactions[Id])

        try processTransaction.createTransaction(createTransactionRequest)
        catch
          case CompanyNotFound(companyName) =>

            assertEquals(companyName, createTransactionRequest.name.toCompanyName)
    }
  }

  test("Should succeed if a company can be found for the given company name") {
    forAll(createTransactionRequestGen, companyUuidGen) { (createTransactionRequest, companyUuid) =>

        val companies = new TestCompanies[Id] {
          override def getCompanyUuidByName(transactionEntity: CompanyName): Id[Option[CompanyUuid]] =
            companyUuid.some
        }

        var insertedTransaction: Option[OpenBankingTransaction] = None

        val transactions = new TestOpenBankingTransactions[Id] {
          override def createTransaction(transaction: OpenBankingTransaction): Id[Unit] = {
            insertedTransaction = transaction.some
            (): Unit
          }
        }

        val processTransaction = new ProcessTransaction[Id](companies, transactions)

        processTransaction.createTransaction(createTransactionRequest)

        assertEquals(insertedTransaction.map(_.amount), createTransactionRequest.amount.some)
        assertEquals(insertedTransaction.map(_.userUuid), createTransactionRequest.userUuid.some)
        assertEquals(insertedTransaction.map(_.companyUuid), companyUuid.some)
    }
  }
}
