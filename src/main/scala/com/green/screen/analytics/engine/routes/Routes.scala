package com.green.screen.analytics.engine.routes

import cats.effect.Concurrent
import cats.syntax.all.*
import com.green.screen.analytics.engine.programs.Programs
import com.green.screen.middlewares.UserType
import org.http4s.AuthedRoutes
import org.typelevel.log4cats.Logger

object Routes:
  def make[F[_]: Concurrent: Logger](programs: Programs[F]): AuthedRoutes[UserType, F] = {
    val transactionRoutes = TransactionRoutes.routes[F](programs.processTransaction)
    val userRoutes        = UserRoutes.routes(programs.getUserScores)

    List(transactionRoutes, userRoutes).reduce(_ <+> _)
  }
end Routes
