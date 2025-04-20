package com.green.screen.analytics.engine.algebras.clients

import cats.effect.Concurrent
import org.http4s.client.Client
import org.typelevel.log4cats.Logger

trait Clients[F[_]]:
  val openAPIBankingClient: OpenAPIBankingClient[F]
end Clients

object Clients:
  def make[F[_]: Concurrent: Logger](client: Client[F]): Clients[F] =
    new Clients[F] {
      override val openAPIBankingClient: OpenAPIBankingClient[F] =
        OpenAPIBankingClient.make[F](client)
    }
end Clients
