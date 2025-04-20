//package com.green.screen.analytics.engine.domain
//
//import com.green.screen.analytics.engine.domain.AccountAccessConsentsStatus.AwaitingAuthorisation
//import eu.timepit.refined.types.string.NonEmptyString
//import io.circe.*
//import io.circe.derivation.*
//import io.circe.derivation.Configuration.default
////import io.circe.generic.semiauto.deriveEncoder
//
//import java.time.Instant
//
//given configuration: Configuration = default.withPascalCaseMemberNames
//
//opaque type Permission = NonEmptyString
//
//object Permission {
//  def apply(nes: NonEmptyString): Permission               = nes
//  extension (permission: Permission) def value: Permission = permission
//  implicit val permissionDecoder: Decoder[Permission] =
//    _.get[Permission]("Permission")
//}
//
//opaque type ExpirationDateTime = Instant
//
//object ExpirationDateTime {
//  def apply(instant: Instant): ExpirationDateTime                              = instant
//  extension (expirationDate: ExpirationDateTime) def value: ExpirationDateTime = expirationDate
//  implicit val expirationDateDecoder: Decoder[ExpirationDateTime] =
//    _.get[ExpirationDateTime]("ExpirationDateTime")
//}
//
//opaque type TransactionFromDateTime = Instant
//
//object TransactionFromDateTime {
//  def apply(instant: Instant): TransactionFromDateTime                                        = instant
//  extension (transactionFromDate: TransactionFromDateTime) def value: TransactionFromDateTime = transactionFromDate
//  implicit val expirationDateDecoder: Decoder[TransactionFromDateTime] =
//    _.get[TransactionFromDateTime]("TransactionFromDateTime")
//}
//opaque type TransactionToDateTime = Instant
//
//object TransactionToDateTime {
//  def apply(instant: Instant): TransactionToDateTime                                      = instant
//  extension (transactionFromDate: TransactionToDateTime) def value: TransactionToDateTime = transactionFromDate
//  implicit val expirationDateDecoder: Decoder[TransactionToDateTime] =
//    _.get[TransactionToDateTime]("TransactionToDateTime")
//}
//
//final case class CreateAccountAccessConsentsRequest(
//    permissions: List[Permission],
//    expirationDateTime: ExpirationDateTime,
//    transactionFromDateTime: TransactionFromDateTime,
//    transactionToDateDateTime: TransactionToDateTime
//)
//
//object CreateAccountAccessConsentsRequest:
//  given createAccountAccessConsentsRequestDecoder: Decoder[CreateAccountAccessConsentsRequest] =
//    _.get[CreateAccountAccessConsentsRequest]("Data")
//end CreateAccountAccessConsentsRequest
//
//enum AccountAccessConsentsStatus(stringRepresentation: String):
//  case AwaitingAuthorisation extends AccountAccessConsentsStatus("AWAU")
//  case Authorised            extends AccountAccessConsentsStatus("AUTH")
//  case Rejected              extends AccountAccessConsentsStatus("RJCT")
//  case Cancelled             extends AccountAccessConsentsStatus("CANC")
//  case Expired               extends AccountAccessConsentsStatus("EXPD")
//
//  given accountAccessStatusDecoder: Encoder[AccountAccessConsentsStatus] = ???
//  given accountAccessStatusEncoder: Decoder[AccountAccessConsentsStatus] = ???
//end AccountAccessConsentsStatus
//
//final case class StatusReason(statusReasonCode: NonEmptyString, statusReasonDescription: NonEmptyString)
//
//opaque type ConsentId = NonEmptyString
//
//object ConsentId {
//  def apply(nes: NonEmptyString): ConsentId = nes
//
//  extension (consentId: ConsentId) def value: ConsentId = consentId
//  implicit val permissionDecoder: Decoder[ConsentId] =
//    _.get[ConsentId]("ConsentId")
//}
//
//final case class CreateAccountAccessConsentsResponse(consentId: String) derives ConfiguredEncoder
