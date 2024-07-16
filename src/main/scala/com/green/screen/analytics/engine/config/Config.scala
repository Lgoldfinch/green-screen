package com.green.screen.analytics.engine.config

import cats.ApplicativeThrow
import pureconfig.*
import pureconfig.ConfigReader.Result
import pureconfig.generic.derivation.*
import cats.syntax.all.*
import pureconfig.error.ConfigReaderFailures

final case class AppConfig(db: DBConfig) derives ConfigReader

case class DBConfig(port: Int, host: String, dbUser: String, password: String, name: String, poolSize: Int)

object ConfigLoader {
  def loadConfig[F[_]: ApplicativeThrow]: F[AppConfig] =
    ConfigSource.default.load[AppConfig].leftMap(err => new RuntimeException(err.prettyPrint())).liftTo[F]
}
