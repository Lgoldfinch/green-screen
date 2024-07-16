package com.green.screen.analytics.engine.generators

import com.green.screen.analytics.engine.domain.*
import eu.timepit.refined.api.RefType
import eu.timepit.refined.types.string.NonEmptyString
import org.scalacheck.Gen

object openBanking:

  val bankPrefixGen: Gen[BankPrefix] = nonEmptyStringGen(nes => RefType.applyRef.unsafeFrom(nes.value + "/"))

  val consentIdGen: Gen[ConsentId] = nonEmptyStringGen(ConsentId.apply)

  val accountAccessConsentsStatusGen: Gen[AccountAccessConsentsStatus] =
    Gen.oneOf(AccountAccessConsentsStatus.values.toList)

  val accountAccessConsentsResponseGen: Gen[AccountAccessConsentsResponse] = for {
    consentId <- consentIdGen
    status    <- accountAccessConsentsStatusGen
  } yield AccountAccessConsentsResponse(consentId, status)

  val permissionGen: Gen[Permission] = Gen.oneOf(Permission.values.toIndexedSeq)

  val expirationDateTimeGen: Gen[ExpirationDateTime] = instantGen.map(ExpirationDateTime.apply)

  val transactionFromDateTimeGen: Gen[TransactionFromDateTime] = instantGen.map(TransactionFromDateTime.apply)

  val transactionToDateTimeGen: Gen[TransactionToDateTime] = instantGen.map(TransactionToDateTime.apply)

  val createAccountAccessConsentsRequestBodyGen: Gen[CreateAccountAccessConsentsRequest] = for {
    permissions           <- nelGen(permissionGen)
    expirationDate        <- expirationDateTimeGen
    transactionFromDate   <- transactionFromDateTimeGen
    transactionToDateTime <- transactionToDateTimeGen
  } yield CreateAccountAccessConsentsRequest(
    CreateAccountAccessConsentsRequestData(permissions, expirationDate, transactionFromDate, transactionToDateTime)
  )

end openBanking
