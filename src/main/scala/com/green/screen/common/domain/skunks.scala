package com.green.screen.common.domain

import eu.timepit.refined.types.string.NonEmptyString
import skunk.*
import skunk.data.Type

object skunks {
  val nesCodec: Codec[NonEmptyString] = Codec.simple[NonEmptyString](
    _.value,
    NonEmptyString.from,
    Type.text
  )
}
