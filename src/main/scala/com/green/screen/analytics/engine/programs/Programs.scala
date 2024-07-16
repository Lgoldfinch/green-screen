package com.green.screen.analytics.engine.programs

import cats.MonadThrow
import com.green.screen.analytics.engine.algebras.Algebras
import com.green.screen.analytics.engine.algebras.clients.Clients
import com.green.screen.common.domain.effects.GenUUID
import org.typelevel.log4cats.Logger

trait Programs[F[_]]:
  val getUserScores: GetUserScores[F]
  val processTransaction: ProcessTransaction[F]
  val createAccountAccessConsent: CreateAccountAccessConsent[F]
end Programs

object Programs:
  def make[F[_]: MonadThrow: Logger: GenUUID](algebras: Algebras[F], clients: Clients[F]): Programs[F] =
    new Programs[F] {
      override val getUserScores: GetUserScores[F] = GetUserScores[F](algebras.users)

      override val processTransaction: ProcessTransaction[F] =
        ProcessTransaction[F](algebras.companies, algebras.transactions)

      override val createAccountAccessConsent: CreateAccountAccessConsent[F] =
        CreateAccountAccessConsent[F](clients.accountAccessConsentClient, algebras.userOpenApiData)
    }
end Programs
