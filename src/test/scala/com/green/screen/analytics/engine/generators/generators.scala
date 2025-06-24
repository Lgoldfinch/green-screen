package com.green.screen.analytics.engine

import cats.data.NonEmptyList
import com.green.screen.analytics.engine.config.config.RefinedUri
import com.green.screen.analytics.engine.domain.common.CreatedAt
import eu.timepit.refined.api.Refined
import eu.timepit.refined.types.all.*
import org.scalacheck.Gen

import java.time.Instant

package object generators:
  def nelGen[A](a: Gen[A]): Gen[NonEmptyList[A]] = for {
    head <- a
    tail <- Gen.listOf(a)
  } yield NonEmptyList.of(head, tail*)

  private val nonEmptyStringGen: Gen[NonEmptyString] =
    for {
      char <- Gen.alphaChar
      str  <- Gen.alphaStr
    } yield NonEmptyString.unsafeFrom(str ++ char.toString)

  def nonEmptyStringGen[A](f: NonEmptyString => A): Gen[A] = nonEmptyStringGen.map(f)

  val nonNegIntGen: Gen[NonNegInt] = Gen.double.map(_.toInt).map(NonNegInt.unsafeFrom)

  val posIntGen: Gen[PosInt] = Gen.long
    .map(_.abs.toInt)
    .map(
      PosInt.unsafeFrom
    )

  val nonNegDoubleGen: Gen[NonNegDouble] = Gen.double.map(NonNegDouble.unsafeFrom)

  val nonNegLongGen: Gen[NonNegLong] = Gen.long.map(_.abs).map(NonNegLong.unsafeFrom)

  def nonNegDoubleGen[A](f: NonNegDouble => A): Gen[A] =
    Gen.double.map(double => NonNegDouble.unsafeFrom(double)).map(f)

  def sequenceListGen[A, B](list: List[A])(f: A => Gen[B]): Gen[List[B]] = Gen.sequence[List[B], B](
    list.map(item => f(item))
  )

  val instantGen: Gen[Instant] = Gen.calendar.map(_.toInstant)

  val createdAtGen: Gen[CreatedAt] = instantGen.map(CreatedAt.apply)

  val refinedUriGen: Gen[RefinedUri] =
    for {
      scheme <- Gen.oneOf("https", "http")
      host   <- Gen.oneOf("example.com")
      path   <- Gen.option(Gen.alphaStr).map(_.getOrElse(""))
      raw = s"$scheme://$host/$path"
    } yield Refined.unsafeApply(raw)
end generators
