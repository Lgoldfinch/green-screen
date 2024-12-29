package com.green.screen.analytics.engine.generators

import com.green.screen.analytics.engine.domain.*
import org.scalacheck.Gen
import companies.companyUuidGen
import com.green.screen.analytics.engine.generators.users.*

object transactions {
  val transactionUuidGen: Gen[TransactionUuid] = Gen.uuid.map(TransactionUuid.apply)

  val transactionAmountGen: Gen[TransactionAmount] = Gen.double.map(TransactionAmount.apply)

  val transactionGen: Gen[UserTransaction] = for {
    transactionUuid   <- transactionUuidGen
    companyUuid       <- companyUuidGen
    userUuid          <- userUuidGen
    transactionAmount <- transactionAmountGen
  } yield UserTransaction(transactionUuid, companyUuid, userUuid, transactionAmount)

  def transactionGen(companyUuid: CompanyUuid, userUuid: UserUuid): Gen[UserTransaction] = for {
    transactionUuid   <- transactionUuidGen
    transactionAmount <- transactionAmountGen
  } yield UserTransaction(transactionUuid, companyUuid, userUuid, transactionAmount)
}
