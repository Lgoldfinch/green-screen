package com.green.screen.ai.domain

import cats.syntax.all.*
import com.green.screen.common.Config.RefinedUri
import eu.timepit.refined.api.Refined
import eu.timepit.refined.pureconfig.*
import eu.timepit.refined.string.*
import eu.timepit.refined.types.string.NonEmptyString
//import org.http4s.{ Uri as Http4sUri, * }
import pureconfig.*
import pureconfig.ConfigReader.Result
import pureconfig.error.*
import pureconfig.generic.derivation.*

object Config {
  opaque type PerplexityBaseUri = RefinedUri

//  TODO Parking this for now also
  object PerplexityBaseUri {
    def apply(value: RefinedUri): PerplexityBaseUri = value

    extension (uri: PerplexityBaseUri) {
      def value: RefinedUri = uri
    }

    given (using configRead: ConfigReader[RefinedUri]): ConfigReader[PerplexityBaseUri] = configRead.from(_)
  }

  final case class PerplexityConfig(
      apiKey: NonEmptyString,
      baseUri: PerplexityBaseUri,
      model: NonEmptyString
  ) derives ConfigReader
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
