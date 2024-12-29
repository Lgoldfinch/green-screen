package com.green.screen.common.domain

import eu.timepit.refined.types.all.*
import skunk.*
import skunk.data.Type

object skunks {
  val nesCodec: Codec[NonEmptyString] = Codec.simple[NonEmptyString](
    _.value,
    NonEmptyString.from,
    Type.text
  )

  val nonNegDoubleCodec: Codec[NonNegDouble] = Codec.simple[NonNegDouble](
    _.value.toString,
    str =>
      str.toDoubleOption
        .toRight(s"String $str was not a Double")
        .flatMap(
          NonNegDouble.from
        ),
    Type.float8
  )
}
