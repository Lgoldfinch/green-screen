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
      for {
        postRequest <- Uri.fromString("account-access-consents").liftTo[F].map { uri =>
          Request[F](Method.POST, uri, headers = headers).withEntity(
            accountRequest
          )
        }
        response <- client.expect[CreateAccountAccessConsentsResponse](postRequest)
      } yield response
    }
  }
}
