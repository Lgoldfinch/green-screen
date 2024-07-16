package com.green.screen.analytics.engine.generators

import com.green.screen.analytics.engine.domain.*
import org.scalacheck.Gen
import companies.companyUuidGen
import com.green.screen.analytics.engine.generators.users.*

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
