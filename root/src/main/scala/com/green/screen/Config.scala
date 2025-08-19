package com.green.screen

import cats.ApplicativeThrow
import cats.syntax.all.*
import com.green.screen.config.AppConfig
import com.green.screen.ai.domain.Config.PerplexityConfig
import eu.timepit.refined.api.Refined
import eu.timepit.refined.pureconfig.*
import eu.timepit.refined.types.all.{ PosInt, UserPortNumber }
import eu.timepit.refined.types.string.NonEmptyString
import pureconfig.*
import pureconfig.ConfigReader.Result
import pureconfig.error.*
import pureconfig.generic.derivation.*

object config {

  opaque type Host = NonEmptyString

  object Host {
    def apply(value: NonEmptyString): Host = value

    extension (h: Host) {
      def value: NonEmptyString = h
    }
  }

  opaque type DbUser = NonEmptyString

  object DbUser {
    def apply(value: NonEmptyString): DbUser = value

    extension (u: DbUser) {
      def value: NonEmptyString = u
    }
  }

  opaque type Password = NonEmptyString

  object Password {
    def apply(value: NonEmptyString): Password = value

    extension (p: Password) {
      def value: NonEmptyString = p
    }
  }

  opaque type DbName = NonEmptyString

  object DbName {
    def apply(value: NonEmptyString): DbName = value

    extension (n: DbName) {
      def value: NonEmptyString = n
    }
  }

  final case class AppConfig(db: DBConfig, perplexity: PerplexityConfig) derives ConfigReader

  final case class DBConfig(
      port: UserPortNumber,
      host: Host,
      dbUser: DbUser,
      password: Password,
      name: DbName,
      poolSize: PosInt
  ) derives ConfigReader
}

object ConfigLoader {
  def loadConfig[F[_]: ApplicativeThrow]: F[AppConfig] =
    ConfigSource.default.load[AppConfig].leftMap(err => new RuntimeException(err.prettyPrint())).liftTo[F]
}
