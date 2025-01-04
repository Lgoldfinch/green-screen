package com.green.screen

import cats.effect.{ MonadCancelThrow, Resource }
import cats.effect.kernel.Temporal
import cats.effect.std.Console
import fs2.io.net.Network
import org.typelevel.log4cats.Logger
import skunk.Session
import skunk.codec.all.text
import skunk.implicits.*
import cats.syntax.all.*
import com.green.screen.analytics.engine.config.DBConfig
import natchez.Trace.Implicits.noop

sealed abstract class AppResources[F[_]](
    val postgres: Resource[F, Session[F]]
)

object AppResources {
  def make[F[_]: Console: Logger: MonadCancelThrow: Network: Temporal](
      dbConfig: DBConfig
  ): Resource[F, AppResources[F]] = {
    def checkPostgresConnection(
        postgres: Resource[F, Session[F]]
    ): F[Unit] = {
      postgres.use { session =>
        session.unique(sql"select version();".query(text)).flatMap { v =>
          Logger[F].info(s"Connected to Postgres $v.")
        }
      }
    }

    def mkPostgresResource: Resource[F, Resource[F, Session[F]]] =
      Session
        .pooled[F](
          host = dbConfig.host,
          port = dbConfig.port,
          user = dbConfig.dbUser,
          database = dbConfig.name,
          password = Some(dbConfig.password),
          max = dbConfig.poolSize
        )
        .evalTap(checkPostgresConnection)

    mkPostgresResource.map(new AppResources[F](_) {})
  }
}
