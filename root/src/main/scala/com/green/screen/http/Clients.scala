package com.green.screen.http

import com.green.screen.banking.algebras.clients.*
import com.green.screen.ai.algebras.clients.*
import cats.effect.Concurrent
import com.green.screen.ai.domain.config.PerplexityConfig
import org.http4s.client.Client
import org.typelevel.log4cats.Logger

trait Clients[F[_]]:
  def accountAccessConsentClient: AccountAccessConsentClient[F]
  def perplexityClient: PerplexityClient[F]
end Clients

object Clients:
  def make[F[_]: {Concurrent, Logger}](client: Client[F], perplexityConfig: PerplexityConfig): Clients[F] =
    new Clients[F] {
      override val accountAccessConsentClient: AccountAccessConsentClient[F] =
        AccountAccessConsentClient.make[F](client)
      override val perplexityClient: PerplexityClient[F] =
        PerplexityClient.make[F](client, perplexityConfig)
    }
end Clients
