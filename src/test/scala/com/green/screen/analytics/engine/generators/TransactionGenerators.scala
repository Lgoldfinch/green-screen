package com.green.screen.analytics.engine.generators

import com.green.screen.analytics.engine.domain.transactions.*
import org.scalacheck.Gen
import CompanyGenerators.companyUuidGen

object TransactionGenerators {
  val transactionUuidGen: Gen[TransactionUuid] = Gen.uuid.map(TransactionUuid.apply)
  
  val transactionAmountGen: Gen[TransactionAmount] = Gen.double.map(TransactionAmount.apply)
  
  val transactionGen: Gen[UserTransaction] = for {
    transactionUuid <- transactionUuidGen
    companyUuid <- companyUuidGen
    transactionAmount <- transactionAmountGen
  } yield UserTransaction(transactionUuid, companyUuid, )
}
