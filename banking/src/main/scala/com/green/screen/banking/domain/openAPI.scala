package com.green.screen.banking.domain

import cats.data.NonEmptyList
import cats.syntax.all.*
import ConsentId.*
import UserOpenApiDataUuid.*
import UserUuid.*
import com.green.screen.common.misc.CreatedAt.*
import com.green.screen.common.additionalSkunkCodecs.*
import com.green.screen.common.misc.*
import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.EndsWith
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.*
import io.circe.derivation.*
import io.circe.derivation.Configuration.default
import skunk.codec.all.*

import java.time.Instant
import java.util.UUID

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
  def apply(instant: Instant): TransactionFromDateTime                        = instant
  extension (transactionFromDate: TransactionFromDateTime) def value: Instant = transactionFromDate
  given transactionFromDateTimeEncoder: Encoder[TransactionFromDateTime]      = Encoder.encodeInstant.contramap(_.value)

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
    permissions: NonEmptyList[Permission],
    expirationDateTime: ExpirationDateTime,
    transactionFromDateTime: TransactionFromDateTime,
    transactionToDateDateTime: TransactionToDateTime
) derives ConfiguredEncoder

enum AccountAccessConsentsStatus(val stringRepresentation: String):
  case AWAU extends AccountAccessConsentsStatus("AWAU")
  case AUTH extends AccountAccessConsentsStatus("AUTH")
  case RJCT extends AccountAccessConsentsStatus("RJCT")
  case CANC extends AccountAccessConsentsStatus("CANC")
  case EXPD extends AccountAccessConsentsStatus("EXPD")

object AccountAccessConsentsStatus {
  given accountAccessConsentsStatusCodec: Codec[AccountAccessConsentsStatus] = ConfiguredEnumCodec.derived
}

final case class StatusReason(statusReasonCode: NonEmptyString, statusReasonDescription: NonEmptyString)

type BankPrefix = String Refined EndsWith["/"]

opaque type ConsentId = NonEmptyString

object ConsentId {
  def apply(nes: NonEmptyString): ConsentId = nes

  extension (consentId: ConsentId) def value: NonEmptyString = consentId

  given Encoder[ConsentId] = nesEncoder.contramap(_.value)

  given Decoder[ConsentId] = nesDecoder.map(ConsentId.apply)

  val consentIdCodec: skunk.Codec[ConsentId] =
    nesCodec.imap(ConsentId.apply)(_.value)
}

final case class AccountAccessConsentsResponse(consentId: ConsentId, status: AccountAccessConsentsStatus)
    derives ConfiguredCodec

opaque type UserOpenApiDataUuid = UUID

object UserOpenApiDataUuid {
  def apply(value: UUID): UserOpenApiDataUuid         = value
  extension (id: UserOpenApiDataUuid) def value: UUID = id

  val userOpenApiDataUuidCodec: skunk.Codec[UserOpenApiDataUuid] =
    uuid.imap(UserOpenApiDataUuid.apply)(_.value)
}

final case class UserOpenApiDataDB(
    uuid: UserOpenApiDataUuid,
    consentId: ConsentId,
    userUuid: UserUuid,
    createdAt: CreatedAt
)

object UserOpenApiDataDB {
  val userOpenApiDataDBCodec: skunk.Codec[UserOpenApiDataDB] =
    (userOpenApiDataUuidCodec, consentIdCodec, userUuidCodec, createdAtCodec).tupled.imap(UserOpenApiDataDB.apply) {
      case UserOpenApiDataDB(uuid, consentId, userUuid, createdAt) =>
        (uuid, consentId, userUuid, createdAt)
    }
}
