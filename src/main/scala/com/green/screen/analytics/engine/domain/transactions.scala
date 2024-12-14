package com.green.screen.analytics.engine.domain

import cats.Show
import cats.syntax.all.*
import com.green.screen.analytics.engine.domain.common.CreatedAt
import com.green.screen.analytics.engine.domain.companies.CompanyUuid
import com.green.screen.analytics.engine.domain.companies.CompanyUuid.companyUuidCodec
import com.green.screen.analytics.engine.domain.transactions.TransactionAmount.transactionAmountCodec
import com.green.screen.analytics.engine.domain.transactions.TransactionUuid.*
import eu.timepit.refined.types.string.NonEmptyString
import io.circe
import io.circe.*
import io.circe.Decoder.Result
import io.circe.syntax.*
import skunk.Codec
import skunk.codec.all.*
import io.circe.generic.semiauto.*

import java.util.UUID
import io.circe.refined.*
object transactions:

  opaque type TransactionUuid = UUID

  object TransactionUuid {
    def apply(uuid: UUID): TransactionUuid = uuid

    extension (t: TransactionUuid)
      def value: UUID = t

    val transactionUuidCodec: Codec[TransactionUuid] = uuid.imap(TransactionUuid.apply)(_.value)
  }

  opaque type TransactionAmount = Double

  object TransactionAmount {
    def apply(amount: Double): TransactionAmount = amount

    val transactionAmountCodec: Codec[TransactionAmount] = float8.imap(TransactionAmount.apply)(
      _.value
    )

    extension (t: TransactionAmount)
      def value: Double = t

    implicit val transactionAmountDecoder: Decoder[TransactionAmount] = _.get[TransactionAmount]("amount")

    implicit val transactionAmountEncoder: Encoder[TransactionAmount] = _.asJson
  }

  // Prefixed User here to differentiate from the Transaction class from Skunk.
  final case class UserTransaction(uuid: TransactionUuid, companyUuid: CompanyUuid, amount: TransactionAmount)

  object UserTransaction {
    val transactionCodec: Codec[UserTransaction] =
      (transactionUuidCodec, companyUuidCodec, transactionAmountCodec).tupled.imap(UserTransaction.apply) {
        case UserTransaction(uuid, companyUuid, amount) => (uuid, companyUuid, amount)
      }

    implicit val transactionShow: Show[UserTransaction] = Show.fromToString
  }

  opaque type TransactionEntity = NonEmptyString

  object TransactionEntity {
    def apply(nes: NonEmptyString): TransactionEntity = nes

    extension (entity: TransactionEntity)
      def value: NonEmptyString = entity

    given transactionEntityDecoder: Decoder[TransactionEntity] with
      override def apply(c: HCursor): Result[TransactionEntity] = c.get[TransactionEntity]("transaction_entity")
  }

  final case class CreateTransactionRequest(name: TransactionEntity, amount: TransactionAmount, createdAt: CreatedAt)

  object CreateTransactionRequest {
    given transactionRequest: Decoder[CreateTransactionRequest] = deriveDecoder[CreateTransactionRequest]
  }
end transactions

