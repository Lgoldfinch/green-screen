package com.green.screen.analytics.engine.domain

import eu.timepit.refined.types.string.NonEmptyString
import io.circe.*
import io.circe.derivation.*
import io.circe.derivation.Configuration.default

import java.time.Instant

given configuration: Configuration = default.withPascalCaseMemberNames

enum Permission:
  case ReadAccountsDetail
  case ReadBalances
  case ReadBeneficiariesDetail
  case ReadDirectDebits
  case ReadProducts
  case ReadStandingOrdersDetail
  case ReadTransactionsCredits
  case ReadTransactionsDebits
  case ReadTransactionsDetail
  case ReadOffers
  case ReadPAN
  case ReadParty
  case ReadPartyPSU
  case ReadScheduledPaymentsDetail
  case ReadStatementsDetail

object Permission {
  given permissionCodec: Codec[Permission] = ConfiguredEnumCodec.derived
}

opaque type ExpirationDateTime = Instant

object ExpirationDateTime {
  def apply(instant: Instant): ExpirationDateTime                              = instant
  extension (expirationDate: ExpirationDateTime) def value: ExpirationDateTime = expirationDate

  given expirationDateEncoder: Encoder[ExpirationDateTime] = Encoder.encodeInstant.contramap(_.value)
}

opaque type TransactionFromDateTime = Instant

object TransactionFromDateTime {
  def apply(instant: Instant): TransactionFromDateTime                                        = instant
  extension (transactionFromDate: TransactionFromDateTime) def value: TransactionFromDateTime = transactionFromDate
  given transactionFromDateTimeEncoder: Encoder[TransactionFromDateTime] = Encoder.encodeInstant.contramap(_.value)

}
opaque type TransactionToDateTime = Instant

object TransactionToDateTime {
  def apply(instant: Instant): TransactionToDateTime                                      = instant
  extension (transactionFromDate: TransactionToDateTime) def value: TransactionToDateTime = transactionFromDate

  given transactionToDateTimeEncoder: Encoder[TransactionToDateTime] = Encoder.encodeInstant.contramap(_.value)
}

final case class CreateAccountAccessConsentsRequest(
    data: CreateAccountAccessConsentsRequestData
) derives ConfiguredEncoder

final case class CreateAccountAccessConsentsRequestData(
    permissions: List[Permission],
    expirationDateTime: ExpirationDateTime,
    transactionFromDateTime: TransactionFromDateTime,
    transactionToDateDateTime: TransactionToDateTime
) derives ConfiguredEncoder

enum AccountAccessConsentsStatus(val stringRepresentation: String):
  case AwaitingAuthorisation extends AccountAccessConsentsStatus("AWAU")
  case Authorised            extends AccountAccessConsentsStatus("AUTH")
  case Rejected              extends AccountAccessConsentsStatus("RJCT")
  case Cancelled             extends AccountAccessConsentsStatus("CANC")
  case Expired               extends AccountAccessConsentsStatus("EXPD")

object AccountAccessConsentsStatus {
  given accountAccessConsentsStatusCodec: Codec[AccountAccessConsentsStatus] = ConfiguredEnumCodec.derived
}

final case class StatusReason(statusReasonCode: NonEmptyString, statusReasonDescription: NonEmptyString)

opaque type ConsentId = NonEmptyString

object ConsentId {
  def apply(nes: NonEmptyString): ConsentId = nes

  extension (consentId: ConsentId) def value: ConsentId = consentId
  implicit val permissionDecoder: Decoder[ConsentId] =
    _.get[ConsentId]("ConsentId")
}

final case class CreateAccountAccessConsentsResponse(consentId: ConsentId, status: AccountAccessConsentsStatus)
    derives ConfiguredDecoder
