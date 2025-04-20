package com.green.screen.analytics.engine.algebras.clients

import cats.effect.kernel.Concurrent
import cats.syntax.all.*
import com.green.screen.analytics.engine.domain.{
  CreateAccountAccessConsentsRequest,
  CreateAccountAccessConsentsResponse
}
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.headers.Accept

trait OpenAPIBankingClient[F[_]] {
  def setAccountAccessConsents(
      accountRequest: CreateAccountAccessConsentsRequest
  ): F[CreateAccountAccessConsentsResponse]
}

object OpenAPIBankingClient {
  def make[F[_]: Concurrent](client: Client[F]): OpenAPIBankingClient[F] = new OpenAPIBankingClient
    with Http4sClientDsl[F] {

    private val headers = Headers(Accept(MediaType.application.json))

    override def setAccountAccessConsents(
        accountRequest: CreateAccountAccessConsentsRequest
    ): F[CreateAccountAccessConsentsResponse] = {

      val postRequest =
        Uri.fromString("account-access-consents").liftTo[F].map { uri =>
          Request[F](Method.POST, uri, headers = headers).withEntity(
            accountRequest
          )
        }

      postRequest.flatMap(client.expect[CreateAccountAccessConsentsResponse])
    }
  }

}

//enum Permission:
//  case ReadAccountsDetail
//  case ReadBalances
//  case ReadBeneficiariesDetail
//  case ReadDirectDebits
//  case ReadProducts
//  case ReadStandingOrdersDetail
//  case ReadTransactionsCredits
//  case ReadTransactionsDebits
//  case ReadTransactionsDetail
//  case ReadOffers
//  case ReadPAN
//  case ReadParty
//  case ReadPartyPSU
//  case ReadScheduledPaymentsDetail
//  case ReadStatementsDetail
//
//object Permission {
//    given Encoder[Permission] = Encoder.encodeString.contramap(_.toString)
//}
////
////opaque type DateTime = OffsetDateTime
////
////object DateTime:
////  def apply(dt: OffsetDateTime): DateTime = dt
////
////  extension (d: DateTime) def value: OffsetDateTime = d
////
////  given Encoder[DateTime] = Encoder.encodeOffsetDateTime.contramap(_.value)
////
////  given Decoder[DateTime] = Decoder.decodeOffsetDateTime.map(DateTime(_))
//
//final case class SetAccountAccessConsentsRequest(
//                                                  data: SetAccountAccessConsentsRequestData
//                                                 )
//
//object SetAccountAccessConsentsRequest {
//  given requestDecoder: Encoder[SetAccountAccessConsentsRequest] = deriveEncoder[SetAccountAccessConsentsRequest]
//}
//
//final case class SetAccountAccessConsentsRequestData(
//                                                      permissions: List[Permission],
//                                                      ExpirationDateTime: DateTime,
//                                                      TransactionFromDateTime: DateTime,
//                                                      TransactionToDateTime: DateTime
//                                                    )
//
//object SetAccountAccessConsentsRequestData {
//  given requestDecoder: Encoder[SetAccountAccessConsentsRequestData] = deriveEncoder[SetAccountAccessConsentsRequestData]
//}
//
//
//final case class SetAccountAccessConsentsResponse()
