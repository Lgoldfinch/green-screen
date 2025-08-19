package com.green.screen.db

import cats.effect.Sync
import cats.syntax.all.*
import com.green.screen.config.DBConfig
import org.flywaydb.core.Flyway
import org.typelevel.log4cats.Logger

final class SqlMigrator[F[_]: Sync: Logger](dbConfig: DBConfig) {
  def run: F[Unit] = {

    val url = s"jdbc:postgresql://${dbConfig.host}:${dbConfig.port}/${dbConfig.name}"

    for {
      _ <- Sync[F].blocking(
        Flyway
          .configure()
          .dataSource(url, dbConfig.dbUser.value.value, dbConfig.password.value.value)
          .load()
          .migrate()
      )
      _ <- Logger[F].info("Running the database migrations.") >>
        Logger[F].info(s"schema: postgres; url: $url")
    } yield ()
  }
}
