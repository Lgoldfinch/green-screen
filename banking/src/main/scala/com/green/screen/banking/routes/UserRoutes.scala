package com.green.screen.banking.routes

import cats.effect.Concurrent
import cats.syntax.all.*
import com.green.screen.banking.domain.users.*
import com.green.screen.banking.domain.users.UserScore.*
import com.green.screen.banking.programs.GetUserScores.UserNotFound
import com.green.screen.banking.programs.{ CreateAccountAccessConsent, GetUserScores }
import com.green.screen.common.auth.UserType
import org.http4s.*
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger

object UserRoutes:
  def routes[F[_]](
      userScores: GetUserScores[F],
      createAccountAccessConsent: CreateAccountAccessConsent[F]
  )(using Concurrent[F], Logger[F]): AuthedRoutes[UserType, F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl.*

    AuthedRoutes.of { case authReq @ GET -> Root / "users" / UUIDVar(userUuid) / "score" as _ =>
      userScores.getScores(UserUuid(userUuid)).flatMap(Ok(_)).recoverWith { case err: UserNotFound =>
        NotFound(err.getMessage)
      }
    }
  }
end UserRoutes
