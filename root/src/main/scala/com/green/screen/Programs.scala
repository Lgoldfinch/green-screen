package com.green.screen

import cats.MonadThrow
import com.green.screen.ai.programs.*
import com.green.screen.banking.programs.*
import com.green.screen.common.effects.GenUUID
import com.green.screen.http.Clients
import org.typelevel.log4cats.Logger

trait Programs[F[_]]:
  val getUserScores: GetUserScores[F]
  val processTransaction: ProcessTransaction[F]
  val createAccountAccessConsent: CreateAccountAccessConsent[F]
  val perplexity: Perplexity[F]
end Programs

object Programs:
  def make[F[_]: MonadThrow: Logger: GenUUID](algebras: Algebras[F], clients: Clients[F]): Programs[F] =
    new Programs[F] {
      override val getUserScores: GetUserScores[F] = GetUserScores[F](algebras.users)

      override val processTransaction: ProcessTransaction[F] =
        ProcessTransaction[F](algebras.companies, algebras.transactions)

      override val createAccountAccessConsent: CreateAccountAccessConsent[F] =
        CreateAccountAccessConsent[F](clients.accountAccessConsentClient, algebras.userOpenApiData)

      override val perplexity: Perplexity[F] =
        Perplexity[F](clients.perplexityClient)
    }
end Programs
