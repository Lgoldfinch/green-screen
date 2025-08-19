package com.green.screen.common

import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.Uri
import eu.timepit.refined.types.string.NonEmptyString

object Config {
  opaque type ApiKey = NonEmptyString

  object ApiKey {
    def apply(value: NonEmptyString): ApiKey = value

    extension (k: ApiKey) {
      def value: NonEmptyString = k
    }
  }

  type RefinedUri = String Refined Uri

  object RefinedUri {
    extension (uri: RefinedUri) {
      def value: String Refined Uri = uri
    }
  }
}
