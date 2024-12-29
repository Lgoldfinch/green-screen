package com.green.screen.analytics.engine

import cats.effect.Concurrent
import com.green.screen.analytics.engine.programs.GetUserScores
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import cats.syntax.all.*
import com.green.screen.analytics.engine.domain.UserUuid
import com.green.screen.analytics.engine.programs.GetUserScores.UserNotFound
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder

object UserRoutes:
  def routes[F[_]: Concurrent: Logger](userScores: GetUserScores[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl.*

    HttpRoutes.of[F] { case req @ GET -> Root / "users" / UUIDVar(userUuid) / "score" =>
      userScores.getScores(UserUuid(userUuid)).flatMap(Ok(_)).recoverWith { case err: UserNotFound =>
        NotFound(err.getMessage)
      }
    }
  }
end UserRoutes
