package com.green.screen.analytics.engine.domain

import cats.Show
import cats.syntax.all.*
import com.green.screen.analytics.engine.domain.TransactionAmount.*
import com.green.screen.analytics.engine.domain.TransactionUuid.*
import com.green.screen.analytics.engine.domain.common.CreatedAt.*
import com.green.screen.analytics.engine.domain.common.{ CreatedAt, nesDecoder }
import com.green.screen.analytics.engine.domain.CompanyUuid.companyUuidCodec
import com.green.screen.analytics.engine.domain.UserUuid.*
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

// Prefixed User here to differentiate from the Transaction class from Skunk.
final case class UserTransaction(
    uuid: TransactionUuid,
    companyUuid: CompanyUuid,
    userUuid: UserUuid,
    amount: TransactionAmount
)

object UserTransaction {
  val transactionCodec: Codec[UserTransaction] =
    (transactionUuidCodec, companyUuidCodec, userUuidCodec, transactionAmountCodec).tupled.imap(
      UserTransaction.apply
    ) { case UserTransaction(uuid, companyUuid, userUuid, amount) =>
      (uuid, companyUuid, userUuid, amount)
    }

  implicit val transactionShow: Show[UserTransaction] = Show.fromToString
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
