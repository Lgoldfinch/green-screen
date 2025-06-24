package com.green.screen.analytics.engine.config

import cats.ApplicativeThrow
import cats.syntax.all.*
import com.green.screen.analytics.engine.config
import com.green.screen.analytics.engine.config.config.AppConfig
import eu.timepit.refined.api.Refined
import eu.timepit.refined.pureconfig.*
import eu.timepit.refined.string.*
import eu.timepit.refined.types.all.{ PosInt, UserPortNumber }
import eu.timepit.refined.types.string.NonEmptyString
import org.http4s.Uri as Http4sUri
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

  opaque type ApiKey = NonEmptyString

  object ApiKey {
    def apply(value: NonEmptyString): ApiKey = value

    extension (k: ApiKey) {
      def value: NonEmptyString = k
    }
  }

  type RefinedUri = String Refined Uri

  opaque type PerplexityBaseUri = RefinedUri

  object PerplexityBaseUri {
    def apply(value: String Refined Uri): PerplexityBaseUri = value

    extension (b: PerplexityBaseUri) {
      def value: String Refined Uri = b
      def toHttp4sUri: Http4sUri    = Http4sUri.unsafeFromString(b.value.toString)
    }
  }

//  TODO Could not get the encoder to work.
//  opaque type PerplexityModel = NonEmptyString
//
//  object PerplexityModel {
//    def apply(value: NonEmptyString): PerplexityModel = value
//
//    extension (model: PerplexityModel) {
//      def value: NonEmptyString = model
//    }
//
//    given (using enc: Encoder[NonEmptyString]): Encoder[PerplexityModel] =
//      enc
//  }

  final case class AppConfig(db: DBConfig, perplexity: PerplexityConfig) derives ConfigReader

  final case class DBConfig(
      port: UserPortNumber,
      host: Host,
      dbUser: DbUser,
      password: Password,
      name: DbName,
      poolSize: PosInt
  ) derives ConfigReader

  final case class PerplexityConfig(
      apiKey: ApiKey,
      baseUri: PerplexityBaseUri,
      model: NonEmptyString
  ) derives ConfigReader
}

object ConfigLoader {
  def loadConfig[F[_]: ApplicativeThrow]: F[AppConfig] =
    ConfigSource.default.load[AppConfig].leftMap(err => new RuntimeException(err.prettyPrint())).liftTo[F]
}
