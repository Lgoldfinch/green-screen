package com.green.screen.banking.routes

import cats.effect.Concurrent
import cats.syntax.all.*
import com.green.screen.banking.domain.CreateTransactionRequest
import com.green.screen.banking.programs.ProcessTransaction.CompanyNotFound
import com.green.screen.banking.programs.ProcessTransaction
import com.green.screen.common.auth.UserType
import org.http4s.AuthedRoutes
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger

object TransactionRoutes:
  def routes[F[_]](
      processTransaction: ProcessTransaction[F]
  )(using Concurrent[F], Logger[F]): AuthedRoutes[UserType, F] = {
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
