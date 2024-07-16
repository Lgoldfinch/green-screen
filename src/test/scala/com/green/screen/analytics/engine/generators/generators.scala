package com.green.screen.analytics.engine

import cats.data.NonEmptyList
import com.green.screen.analytics.engine.domain.common.CreatedAt
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

  val nonNegDoubleGen: Gen[NonNegDouble] = Gen.double.map(NonNegDouble.unsafeFrom)

  def nonNegDoubleGen[A](f: NonNegDouble => A): Gen[A] =
    Gen.double.map(double => NonNegDouble.unsafeFrom(double)).map(f)

  def sequenceListGen[A, B](list: List[A])(f: A => Gen[B]): Gen[List[B]] = Gen.sequence[List[B], B](
    list.map(item => f(item))
  )

  val instantGen: Gen[Instant] = Gen.calendar.map(_.toInstant)

  val createdAtGen: Gen[CreatedAt] = instantGen.map(CreatedAt.apply)
end generators
