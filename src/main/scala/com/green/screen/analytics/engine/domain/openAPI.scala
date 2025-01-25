package com.green.screen.analytics.engine.domain

import eu.timepit.refined.types.string.NonEmptyString
import io.circe.Decoder

import java.time.Instant

opaque type Permission = NonEmptyString

object Permission {
  def apply(nes: NonEmptyString): Permission = nes
  extension (permission : Permission) def value: Permission = permission
  implicit val permissionDecoder: Decoder[Permission] =
    _.get[Permission]("Permission")
}

opaque type ExpirationDateTime = Instant

object ExpirationDateTime {
  def apply(instant: Instant): ExpirationDateTime = instant
  extension (expirationDate : ExpirationDateTime) def value: ExpirationDateTime = expirationDate
  implicit val expirationDateDecoder: Decoder[ExpirationDateTime] =
    _.get[ExpirationDateTime]("ExpirationDateTime")
}

opaque type TransactionFromDateTime = Instant

object TransactionFromDateTime {
  def apply(instant: Instant): TransactionFromDateTime = instant
  extension (transactionFromDate : TransactionFromDateTime) def value: TransactionFromDateTime = transactionFromDate
  implicit val expirationDateDecoder: Decoder[TransactionFromDateTime] =
    _.get[TransactionFromDateTime]("TransactionFromDateTime")
}
opaque type TransactionToDateTime = Instant

object TransactionToDateTime {
  def apply(instant: Instant): TransactionToDateTime = instant
  extension (transactionFromDate : TransactionToDateTime) def value: TransactionToDateTime = transactionFromDate
  implicit val expirationDateDecoder: Decoder[TransactionToDateTime] =
    _.get[TransactionToDateTime]("TransactionToDateTime")
}


final case class CreateAccountAccessConsentsRequest(
                                                     permissions: List[Permission],
                                                     expirationDateTime: ExpirationDateTime,
                                                     transactionFromDateTime: TransactionFromDateTime,
                                                     transactionToDateDateTime: TransactionToDateTime
                                                   )

object CreateAccountAccessConsentsRequest:
      given createAccountAccessConsentsRequestDecoder: Decoder[CreateAccountAccessConsentsRequest] = _.get[CreateAccountAccessConsentsRequest]("Data")

end CreateAccountAccessConsentsRequest 