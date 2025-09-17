package com.green.screen.banking.generators

import org.scalacheck.Gen
import com.green.screen.banking.generators.users.*
import com.green.screen.banking.domain.companies.CompanyUuid
import com.green.screen.banking.domain.transactions.{TransactionEntity, *}
import com.green.screen.banking.domain.users.*
import com.green.screen.common.generators.*
import com.green.screen.banking.generators.companies.*

object transactions {
  val transactionUuidGen: Gen[TransactionUuid] = Gen.uuid.map(TransactionUuid.apply)

  val transactionAmountGen: Gen[TransactionAmount] = Gen.double.map(TransactionAmount.apply)
  
  val transactionEntityGen: Gen[TransactionEntity] = nonEmptyStringGen(TransactionEntity.apply)
  
  val OpenBankingTransactionGen: Gen[OpenBankingTransaction] = for {
    transactionUuid   <- transactionUuidGen
    companyUuid       <- companyUuidGen
    userUuid          <- userUuidGen
    transactionAmount <- transactionAmountGen
    createdAt         <- createdAtGen
  } yield OpenBankingTransaction(transactionUuid, companyUuid, userUuid, transactionAmount, createdAt)

  def OpenBankingTransactionGen(companyUuid: CompanyUuid, userUuid: UserUuid): Gen[OpenBankingTransaction] = for {
    transactionUuid   <- transactionUuidGen
    transactionAmount <- transactionAmountGen
    createdAt         <- createdAtGen
  } yield OpenBankingTransaction(transactionUuid, companyUuid, userUuid, transactionAmount, createdAt)
  
  val createTransactionRequestGen: Gen[CreateTransactionRequest] = for {
    name        <- transactionEntityGen
    userUuid    <- userUuidGen
    amount      <- transactionAmountGen
  } yield CreateTransactionRequest(name, amount, userUuid)
}
