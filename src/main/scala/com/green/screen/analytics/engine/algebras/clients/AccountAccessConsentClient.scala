package com.green.screen.analytics.engine.algebras.clients

import cats.effect.kernel.Concurrent
import cats.syntax.all.*
import com.green.screen.analytics.engine.domain.{CreateAccountAccessConsentsRequest, CreateAccountAccessConsentsResponse}
import eu.timepit.refined.api.Refined
import eu.timepit.refined.types.string
import eu.timepit.refined.types.string.NonEmptyString
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.headers.Accept
import org.http4s.server.Router
import eu.timepit.refined.string.*
import org.http4s

trait AccountAccessConsentClient[F[_]] {
  def setAccountAccessConsents(
      accountRequest: CreateAccountAccessConsentsRequest
  ): F[CreateAccountAccessConsentsResponse]
}

type BankPrefix = String Refined EndsWith["/"]

object AccountAccessConsentClient {
  // We need to have client that changes the root depending on the bank
  def make[F[_]: Concurrent](client: Client[F], bankPrefixPath: BankPrefix): AccountAccessConsentClient[F] = new AccountAccessConsentClient
    with Http4sClientDsl[F] {

    private val headers = Headers(Accept(MediaType.application.json))

    override def setAccountAccessConsents(
        accountRequest: CreateAccountAccessConsentsRequest
    ): F[CreateAccountAccessConsentsResponse] = {
      for {
        postRequest <- http4s.Uri.fromString(bankPrefixPath.value + "account-access-consents")
          .liftTo[F].map { uri =>
          Request[F](Method.POST, uri, headers = headers).withEntity(
            accountRequest
          )
        }
        response <- client.expect[CreateAccountAccessConsentsResponse](postRequest)
      } yield response
    }
  }
}
