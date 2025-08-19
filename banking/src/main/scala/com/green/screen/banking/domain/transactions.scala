package com.green.screen.banking.domain

import cats.Show
import cats.syntax.all.*
import com.green.screen.common.misc.*
import com.green.screen.banking.domain.TransactionAmount.*
import com.green.screen.banking.domain.TransactionUuid.*
import com.green.screen.common.misc.CreatedAt.createdAtCodec
import com.green.screen.banking.domain.CompanyUuid.companyUuidCodec
import com.green.screen.banking.domain.UserUuid.*
import eu.timepit.refined.types.string.NonEmptyString
import io.circe
import io.circe.*
import io.circe.generic.semiauto.*
import skunk.Codec
import skunk.codec.all.*

import java.util.UUID

opaque type TransactionUuid = UUID

object TransactionUuid {
  def apply(uuid: UUID): TransactionUuid = uuid

  extension (t: TransactionUuid) def value: UUID = t

  val transactionUuidCodec: Codec[TransactionUuid] = uuid.imap(TransactionUuid.apply)(_.value)
}

opaque type TransactionAmount = Double

object TransactionAmount {
  def apply(amount: Double): TransactionAmount = amount

  val transactionAmountCodec: Codec[TransactionAmount] = float8.imap(TransactionAmount.apply)(
    _.value
  )

  extension (t: TransactionAmount) def value: Double = t

  implicit val transactionAmountDecoder: Decoder[TransactionAmount] = Decoder.decodeDouble.map(TransactionAmount.apply)
}

final case class OpenAPITransaction(
    uuid: TransactionUuid,
    companyUuid: CompanyUuid,
    userUuid: UserUuid,
    amount: TransactionAmount,
    createdAt: CreatedAt
)

object OpenAPITransaction {
  val openAPITransactionCodec: Codec[OpenAPITransaction] =
    (transactionUuidCodec, companyUuidCodec, userUuidCodec, transactionAmountCodec, createdAtCodec).tupled.imap(
      OpenAPITransaction.apply
    ) { case OpenAPITransaction(uuid, companyUuid, userUuid, amount, createdAt) =>
      (uuid, companyUuid, userUuid, amount, createdAt)
    }

  implicit val openAPITransactionShow: Show[OpenAPITransaction] = Show.fromToString
}

opaque type TransactionEntity = NonEmptyString

object TransactionEntity {
  def apply(nes: NonEmptyString): TransactionEntity = nes

  extension (entity: TransactionEntity) {
    def value: NonEmptyString      = entity
    def toCompanyName: CompanyName = CompanyName(entity)
  }

  implicit val transactionEntityDecoder: Decoder[TransactionEntity] = nesDecoder.map(TransactionEntity.apply)
}

final case class CreateTransactionRequest(
    name: TransactionEntity,
    amount: TransactionAmount,
    userUuid: UserUuid,
    createdAt: CreatedAt
)

object CreateTransactionRequest {
  given transactionRequest: Decoder[CreateTransactionRequest] = deriveDecoder[CreateTransactionRequest]
}
