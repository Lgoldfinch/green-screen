package com.green.screen.analytics.engine.algebras.clients

import cats.MonadThrow
import org.http4s.*
import org.http4s.circe.*
import cats.syntax.all.*
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.headers.Accept
import org.http4s.client.Client
import org.http4s.dsl.io.GET

trait OpenAPIBankingClient[F[_]] {
  def setAccountAccessConsents(): F[String]
}

object OpenAPIBankingClient {
  def make[F[_]: MonadThrow](client: Client[F]): OpenAPIBankingClient[F] = new OpenAPIBankingClient
    with Http4sClientDsl[F] {

    private val headers = Headers(Accept(MediaType.application.json))

    override def setAccountAccessConsents(): F[Unit] =
      Uri.fromString("account-access-consents").liftTo[F].flatMap { uri =>
        val request = Request[F](Method.POST, uri, headers = headers)
        ???
      }
  }
}
