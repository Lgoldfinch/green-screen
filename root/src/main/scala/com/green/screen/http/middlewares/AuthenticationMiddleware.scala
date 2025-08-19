package com.green.screen.http.middlewares

import cats.data.{ Kleisli, OptionT }
import cats.effect.kernel.Sync
import cats.syntax.all.*
import com.green.screen.common.auth.UserType
import com.green.screen.common.auth.UserType.Admin
import org.http4s.Request
import org.http4s.server.AuthMiddleware
import org.typelevel.ci.CIString

class AuthenticationMiddleware[F[_]: Sync] {
  private val authenticate: Kleisli[OptionT[F, *], Request[F], UserType] =
    Kleisli { request =>
      // Will be implementing JWT authentication eventually.
      val apiKeyOpt = request.headers.get(CIString("top-secret-api-key")).map(_.head)

      val userOpt = apiKeyOpt.flatMap(key =>
        key.value match
          case "makemerich" => Admin.some
          case _            => None
      )

      OptionT.fromOption[F](userOpt)
    }

  def authedMiddleware: AuthMiddleware[F, UserType] = AuthMiddleware(authenticate)
}
