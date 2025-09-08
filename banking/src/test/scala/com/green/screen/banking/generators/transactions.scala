package com.green.screen.banking.generators

import org.scalacheck.Gen
import com.green.screen.banking.generators.users.*
import com.green.screen.banking.domain.companies.CompanyUuid
import com.green.screen.banking.domain.transactions.*
import com.green.screen.banking.domain.users.*
import com.green.screen.common.generators.*
import com.green.screen.banking.generators.companies.*

object transactions {
  val transactionUuidGen: Gen[TransactionUuid] = Gen.uuid.map(TransactionUuid.apply)

  val transactionAmountGen: Gen[TransactionAmount] = Gen.double.map(TransactionAmount.apply)

  val openAPITransactionGen: Gen[OpenAPITransaction] = for {
    transactionUuid   <- transactionUuidGen
    companyUuid       <- companyUuidGen
    userUuid          <- userUuidGen
    transactionAmount <- transactionAmountGen
    createdAt         <- createdAtGen
  } yield OpenAPITransaction(transactionUuid, companyUuid, userUuid, transactionAmount, createdAt)

  def openAPITransactionGen(companyUuid: CompanyUuid, userUuid: UserUuid): Gen[OpenAPITransaction] = for {
    transactionUuid   <- transactionUuidGen
    transactionAmount <- transactionAmountGen
    createdAt         <- createdAtGen
  } yield OpenAPITransaction(transactionUuid, companyUuid, userUuid, transactionAmount, createdAt)
}
