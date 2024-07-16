package com.green.screen.analytics.engine

import cats.effect.Sync
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import skunk.Session
import skunk.implicits.sql
import cats.syntax.all.*
import cats.effect.kernel.Resource
import skunk.implicits._
import skunk.codec.all._

object AnalyticsEngineRoutes {
  def analyticsRoutes[F[_] : Sync](str: String, session: Resource[F, Session[F]]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl.*
    HttpRoutes.of[F] {
      case GET -> Root / "analytics" =>
        session.use(s => s.unique(sql"select current_date".query(date))).map (
          d => println(s"The current date is $d.")
        ) >> Ok(str)
    }
  }
}