package com.green.screen.analytics.engine.routes

import cats.effect.Concurrent
import cats.syntax.all.*
import com.green.screen.analytics.engine.domain.CreateTransactionRequest
import com.green.screen.analytics.engine.programs.ProcessTransaction
import com.green.screen.analytics.engine.programs.ProcessTransaction.CompanyNotFound
import com.green.screen.middlewares.UserType
import org.http4s.AuthedRoutes
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger

object TransactionRoutes:
  def routes[F[_]: Concurrent: Logger](processTransaction: ProcessTransaction[F]): AuthedRoutes[UserType, F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl.*

    AuthedRoutes.of { case authReq @ POST -> Root / "transactions" as user =>
      authReq.req
        .as[CreateTransactionRequest]
        .flatMap(createTransactionRequest =>
          processTransaction.createTransaction(createTransactionRequest).flatMap(Created(_)).recoverWith {
            case err: CompanyNotFound =>
              NotFound(err.getMessage)
          }
        )
    }
  }
end TransactionRoutes
