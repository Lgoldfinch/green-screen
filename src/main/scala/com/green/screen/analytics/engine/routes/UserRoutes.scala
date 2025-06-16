package com.green.screen.analytics.engine.routes

import cats.effect.Concurrent
import cats.syntax.all.*
import com.green.screen.analytics.engine.domain.*
import com.green.screen.analytics.engine.programs.GetUserScores.UserNotFound
import com.green.screen.analytics.engine.programs.{ CreateAccountAccessConsent, GetUserScores }
import com.green.screen.middlewares.UserType
import org.http4s.AuthedRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger

object UserRoutes:
  def routes[F[_]: Concurrent: Logger](
      userScores: GetUserScores[F],
      createAccountAccessConsent: CreateAccountAccessConsent[F]
  ): AuthedRoutes[UserType, F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl.*

    AuthedRoutes.of { case authReq @ GET -> Root / "users" / UUIDVar(userUuid) / "score" as _ =>
      userScores.getScores(UserUuid(userUuid)).flatMap(Ok(_)).recoverWith { case err: UserNotFound =>
        NotFound(err.getMessage)
      }
    }
  }
end UserRoutes
