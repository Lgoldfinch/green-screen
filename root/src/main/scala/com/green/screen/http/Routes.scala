package com.green.screen.http

import cats.ApplicativeThrow
import cats.effect.Concurrent
import cats.syntax.all.*
import com.green.screen.Programs
import com.green.screen.ai.routes.*
import com.green.screen.banking.domain.openBanking.BankPrefix
import com.green.screen.banking.routes.*
import com.green.screen.common.auth.UserType
import eu.timepit.refined.api.RefType
import org.http4s.AuthedRoutes
import org.typelevel.log4cats.Logger

object Routes:
  def make[F[_]: {Concurrent, Logger, ApplicativeThrow}](programs: Programs[F]): F[AuthedRoutes[UserType, F]] = {
    for {
      santanderPrefix <- RefType
        .applyRef[BankPrefix]("santander/")
        .leftMap(RuntimeException(_))
        .liftTo[F]
      companies         = CompanyRoutes.routes(programs.createCompany)
      userRoutes        = UserRoutes.routes(programs.getUserScores)
      transactionRoutes = TransactionRoutes.routes[F](programs.processTransaction)
      perplexity        = PerplexityRoutes.make[F](programs.perplexity)
    } yield List(companies, transactionRoutes, userRoutes, perplexity).reduce(_ <+> _)
  }
end Routes
