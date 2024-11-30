package com.green.screen.analytics.engine.generators

import com.green.screen.analytics.engine.domain.companies.CompanyUuid
import com.green.screen.analytics.engine.domain.transactions.*
import org.scalacheck.Gen
import companies.companyUuidGen

object transactions {
  val transactionUuidGen: Gen[TransactionUuid] = Gen.uuid.map(TransactionUuid.apply)
  
  val transactionAmountGen: Gen[TransactionAmount] = Gen.double.map(TransactionAmount.apply)
  
  val transactionGen: Gen[UserTransaction] = for {
    transactionUuid <- transactionUuidGen
    companyUuid <- companyUuidGen
    transactionAmount <- transactionAmountGen
  } yield UserTransaction(transactionUuid, companyUuid, transactionAmount) 
  
  def transactionGen(companyUuid: CompanyUuid): Gen[UserTransaction] = for {
    transactionUuid <- transactionUuidGen
    transactionAmount <- transactionAmountGen
  } yield UserTransaction(transactionUuid, companyUuid, transactionAmount)
}
