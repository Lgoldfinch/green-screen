package com.green.screen.analytics.engine.algebras.clients

import cats.effect.Concurrent
import com.green.screen.analytics.engine.domain.BankPrefix
import org.http4s.client.Client
import org.typelevel.log4cats.Logger

trait Clients[F[_]]:
  def accountAccessConsentClient(bankPrefix: BankPrefix): AccountAccessConsentClient[F]
end Clients

object Clients:
  def make[F[_]: Concurrent: Logger](client: Client[F]): Clients[F] =
    new Clients[F] {
      override def accountAccessConsentClient(bankPrefix: BankPrefix): AccountAccessConsentClient[F] =
        AccountAccessConsentClient.make[F](client, bankPrefix)
    }
end Clients
