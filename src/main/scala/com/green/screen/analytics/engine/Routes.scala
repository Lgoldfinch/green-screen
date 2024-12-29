package com.green.screen.analytics.engine

import cats.effect.Concurrent
import com.green.screen.analytics.engine.programs.Programs
import org.http4s.HttpRoutes
import cats.syntax.all.*
import org.typelevel.log4cats.Logger

object Routes:
  def make[F[_]: Concurrent: Logger](programs: Programs[F]): HttpRoutes[F] = {
    val transactionRoutes = TransactionRoutes.routes[F](programs.processTransaction)
    val userRoutes        = UserRoutes.routes(programs.getUserScores)

    List(transactionRoutes, userRoutes).reduce(_ <+> _)
  }
end Routes
