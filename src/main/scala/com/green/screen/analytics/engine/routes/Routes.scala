package com.green.screen.analytics.engine.routes

import cats.ApplicativeThrow
import cats.effect.Concurrent
import cats.syntax.all.*
import com.green.screen.analytics.engine.domain.BankPrefix
import com.green.screen.analytics.engine.programs.Programs
import com.green.screen.middlewares.UserType
import eu.timepit.refined.api.RefType
import org.http4s.AuthedRoutes
import org.typelevel.log4cats.Logger

object Routes:
  def make[F[_]: Concurrent: Logger: ApplicativeThrow](programs: Programs[F]): F[AuthedRoutes[UserType, F]] = {
    for {
      santanderPrefix <- RefType
        .applyRef[BankPrefix]("santander/")
        .leftMap(errMsg => new RuntimeException(errMsg))
        .liftTo[F]
      userRoutes        = UserRoutes.routes(programs.getUserScores, programs.createAccountAccessConsent)
      transactionRoutes = TransactionRoutes.routes[F](programs.processTransaction)
      perplexity        = PerplexityRoutes.make[F](programs.perplexity)
    } yield List(transactionRoutes, userRoutes, perplexity).reduce(_ <+> _)
  }
end Routes
