package com.green.screen

import org.flywaydb.core.Flyway
import cats.effect.Sync
import cats.syntax.all._
import org.typelevel.log4cats.Logger

final class SqlMigrator[F[_]: Sync: Logger](url: String) {
  def run: F[Unit] = {
    for {
      _ <- Sync[F].blocking(
        Flyway
          .configure()
          .dataSource(url, "postgres", "password")
          .load()
          .migrate()
      )
      _ <- Logger[F].info("Running the database migrations.") >>
        Logger[F].info(s"schema: postgres; url: $url")
    } yield ()
  }
}
