package com.green.screen

import cats.effect.Resource
import cats.effect.kernel.{ Async, Temporal }
import cats.effect.std.Console
import fs2.io.net.Network
import org.typelevel.log4cats.Logger
import skunk.Session
import skunk.codec.all.text
import skunk.implicits.*
import cats.syntax.all.*
import config.DBConfig
import natchez.Trace.Implicits.noop
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder

sealed abstract class AppResources[F[_]](
    val postgres: Resource[F, Session[F]],
    val client: Client[F]
)

object AppResources {
  def make[F[_]: Console: Logger: Async: Network: Temporal](
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

    def mkPostgresResource: Resource[F, Resource[F, Session[F]]] = {
      Session
        .pooled[F](
          host = dbConfig.host.value.value,
          port = dbConfig.port.value,
          user = dbConfig.dbUser.value.value,
          database = dbConfig.name.value.value,
          password = Some(dbConfig.password.value.value),
          max = dbConfig.poolSize.value
        )
        .evalTap(checkPostgresConnection)
    }

    def mkEmberClient: Resource[F, Client[F]] =
      EmberClientBuilder.default[F].build

    (mkPostgresResource, mkEmberClient).mapN(new AppResources[F](_, _) {})
  }
}
