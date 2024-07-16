package com.environ.mental.analytics.engine

import cats.effect.Sync
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object AnalyticsEngineRoutes {
  def analyticsRoutes[F[_] : Sync](str: String): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl.*
    HttpRoutes.of[F] {
      case GET -> Root / "analytics" =>
        Ok(str)
    }
  }
}