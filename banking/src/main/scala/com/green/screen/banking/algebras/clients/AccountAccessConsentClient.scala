package com.green.screen.banking.algebras.clients

import cats.effect.kernel.Concurrent
import cats.syntax.all.*
import com.green.screen.banking.domain.*
import com.green.screen.banking.domain.{
  AccountAccessConsentsResponse,
  BankPrefix,
  ConsentId,
  CreateAccountAccessConsentsRequest
}
import eu.timepit.refined.api.Refined
import org.http4s
import org.http4s.*
import org.http4s.Status.{ BadRequest, NotFound }
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.client.{ Client, UnexpectedStatus }
import org.http4s.headers.`Content-Type`
import org.typelevel.log4cats.Logger

import scala.util.control.NoStackTrace

trait AccountAccessConsentClient[F[_]]:
  def setAccountAccessConsent(
      accountRequest: CreateAccountAccessConsentsRequest,
      bankPrefixPath: BankPrefix
  ): F[AccountAccessConsentsResponse]

  def getAccountAccessConsent(consentId: ConsentId, bankPrefixPath: BankPrefix): F[AccountAccessConsentsResponse]
end AccountAccessConsentClient

object AccountAccessConsentClient:
  def make[F[_]: Concurrent: Logger](client: Client[F]): AccountAccessConsentClient[F] =
    new AccountAccessConsentClient with Http4sClientDsl[F] {

      private val handleErrors: PartialFunction[Throwable, Throwable] = {
        case UnexpectedStatus(BadRequest, _, requestUri) =>
          AccountAccessConsentClientError(
            s"Was unable to get/set account access consent for request uri $requestUri, consent id was invalid"
          )
        case UnexpectedStatus(NotFound, _, requestUri) =>
          AccountAccessConsentClientError(
            s"Was unable to get/set account access consent for request uri $requestUri, endpoint doesn't exist"
          )
      }

      override def setAccountAccessConsent(
          accountRequest: CreateAccountAccessConsentsRequest,
          bankPrefixPath: BankPrefix
      ): F[AccountAccessConsentsResponse] = {
        for {
          postRequest <- http4s.Uri
            .fromString(bankPrefixPath.value + "account-access-consents")
            .liftTo[F]
            .map { uri =>
              Request[F](Method.POST, uri).withEntity(
                accountRequest
              )
            }
          response <- client.expect[AccountAccessConsentsResponse](postRequest).adaptError(handleErrors)
        } yield response
      }

      override def getAccountAccessConsent(
          consentId: ConsentId,
          bankPrefixPath: BankPrefix
      ): F[AccountAccessConsentsResponse] =
        for {
          getRequest <- http4s.Uri
            .fromString(bankPrefixPath.value + s"account-access-consents/$consentId")
            .liftTo[F]
            .map { uri =>
              Request[F](Method.GET, uri).withContentType(`Content-Type`(MediaType.application.json))
            }
          response <-
            client
              .expect[AccountAccessConsentsResponse](getRequest)
              .adaptError(handleErrors)
        } yield response
    }
end AccountAccessConsentClient

final class AccountAccessConsentClientError(msg: String) extends NoStackTrace {
  override def getMessage: String = msg
}
