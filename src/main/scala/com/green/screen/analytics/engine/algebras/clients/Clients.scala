package com.green.screen.analytics.engine.algebras.clients

import cats.effect.Concurrent
import org.http4s.client.Client
import org.typelevel.log4cats.Logger

trait Clients[F[_]]:
  val openAPIBankingClient: AccountAccessConsentClient[F]
end Clients

object Clients:
  def make[F[_]: Concurrent: Logger](client: Client[F]): Clients[F] =
    new Clients[F] {
      override val openAPIBankingClient: AccountAccessConsentClient[F] =
        AccountAccessConsentClient.make[F](client)
    }
end Clients
