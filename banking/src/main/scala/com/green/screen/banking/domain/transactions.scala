package com.green.screen.banking.domain

import cats.Show
import cats.syntax.all.*
import com.green.screen.banking.domain.users.*
import com.green.screen.banking.domain.companies.*
import com.green.screen.common.misc.*
import com.green.screen.common.misc.CreatedAt.createdAtCodec
import eu.timepit.refined.types.string.NonEmptyString
import io.circe
import io.circe.*
import io.circe.derivation.Configuration.default
import io.circe.derivation.{ Configuration, ConfiguredDecoder }
import io.circe.refined.*
import skunk.Codec
import skunk.codec.all.*

import java.util.UUID

object transactions {

  given configuration: Configuration = default.withPascalCaseMemberNames

  opaque type TransactionUuid = UUID

  object TransactionUuid {
    def apply(uuid: UUID): TransactionUuid         = uuid
    extension (t: TransactionUuid) def value: UUID = t

    val transactionUuidCodec: Codec[TransactionUuid] = uuid.imap(TransactionUuid.apply)(_.value)
  }

  val transactionUuidCodec: Codec[TransactionUuid] = uuid.imap(TransactionUuid.apply)(TransactionUuid.value)

  opaque type TransactionAmount = Double

  object TransactionAmount {
    def apply(amount: Double): TransactionAmount       = amount
    extension (t: TransactionAmount) def value: Double = t

    given Decoder[TransactionAmount] = Decoder.decodeDouble.map(TransactionAmount.apply)
  }

  val transactionAmountCodec: Codec[TransactionAmount] = float8.imap(TransactionAmount.apply)(
    TransactionAmount.value
  )

  final case class OpenAPITransaction(
      uuid: TransactionUuid,
      companyUuid: CompanyUuid,
      userUuid: UserUuid,
      amount: TransactionAmount,
      createdAt: CreatedAt
  )

  object OpenAPITransaction {
    given Show[OpenAPITransaction] = Show.fromToString
  }

  val openAPITransactionCodec: Codec[OpenAPITransaction] =
    (transactionUuidCodec, companyUuidCodec, userUuidCodec, transactionAmountCodec, createdAtCodec).tupled.imap(
      OpenAPITransaction.apply
    ) { case OpenAPITransaction(uuid, companyUuid, userUuid, amount, createdAt) =>
      (uuid, companyUuid, userUuid, amount, createdAt)
    }

  opaque type TransactionEntity = NonEmptyString

  object TransactionEntity {
    def apply(nes: NonEmptyString): TransactionEntity = nes

    extension (entity: TransactionEntity) {
      def value: NonEmptyString      = entity
      def toCompanyName: CompanyName = CompanyName(entity)
    }
  }

  final case class CreateTransactionRequest(
      name: TransactionEntity,
      amount: TransactionAmount,
      userUuid: UserUuid,
      createdAt: CreatedAt
  ) derives ConfiguredDecoder
}
