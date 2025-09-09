package com.green.screen.banking.domain

import cats.data.NonEmptyList
import cats.syntax.all.*
import com.green.screen.banking.domain.users.*
import com.green.screen.common.misc.CreatedAt.*
import com.green.screen.common.db.*
import com.green.screen.common.misc.*
import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.EndsWith
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.*
import io.circe.derivation.*
import io.circe.derivation.Configuration.default
import skunk.codec.all.*
import io.circe.refined.*

import java.time.Instant
import java.util.UUID

object openBanking {

  given configuration: Configuration = default.withPascalCaseMemberNames

  enum Permission:
    case ReadAccountsDetail, ReadBalances, ReadBeneficiariesDetail, ReadDirectDebits, ReadProducts,
      ReadStandingOrdersDetail, ReadTransactionsCredits, ReadTransactionsDebits, ReadTransactionsDetail, ReadOffers,
      ReadPAN, ReadParty, ReadPartyPSU, ReadScheduledPaymentsDetail, ReadStatementsDetail

  object Permission {
    given permissionCodec: Codec[Permission] = ConfiguredEnumCodec.derived
  }

  opaque type ExpirationDateTime = Instant

  object ExpirationDateTime {
    def apply(instant: Instant): ExpirationDateTime = instant

    extension (expirationDate: ExpirationDateTime) def value: ExpirationDateTime = expirationDate
  }

  opaque type TransactionFromDateTime = Instant

  object TransactionFromDateTime {
    def apply(instant: Instant): TransactionFromDateTime = instant

    extension (transactionFromDate: TransactionFromDateTime) def value: Instant = transactionFromDate
  }

  opaque type TransactionToDateTime = Instant

  object TransactionToDateTime {
    def apply(instant: Instant): TransactionToDateTime = instant

    extension (transactionFromDate: TransactionToDateTime) def value: TransactionToDateTime = transactionFromDate
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
  }

  val consentIdCodec: skunk.Codec[ConsentId] =
    nesCodec.imap(ConsentId.apply)(ConsentId.value)

  final case class AccountAccessConsentsResponse(consentId: ConsentId, status: AccountAccessConsentsStatus)
      derives ConfiguredCodec

  opaque type UserOpenBankingDataUuid = UUID

  object UserOpenBankingDataUuid {
    def apply(value: UUID): UserOpenBankingDataUuid = value

    extension (id: UserOpenBankingDataUuid) def value: UUID = id
  }

  val userOpenBankingDataUuidCodec: skunk.Codec[UserOpenBankingDataUuid] =
    uuid.imap(UserOpenBankingDataUuid.apply)(UserOpenBankingDataUuid.value)

  final case class UserOpenBankingDataDB(
      uuid: UserOpenBankingDataUuid,
      consentId: ConsentId,
      userUuid: UserUuid,
      createdAt: CreatedAt
  )

  val userOpenBankingDataDBCodec: skunk.Codec[UserOpenBankingDataDB] =
    (userOpenBankingDataUuidCodec, consentIdCodec, userUuidCodec, createdAtCodec).tupled.imap(
      UserOpenBankingDataDB.apply
    ) { case UserOpenBankingDataDB(uuid, consentId, userUuid, createdAt) =>
      (uuid, consentId, userUuid, createdAt)
    }

}
