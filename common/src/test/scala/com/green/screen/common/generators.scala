package com.green.screen.common

import cats.data.NonEmptyList
import com.green.screen.common.config.RefinedUri
import com.green.screen.common.misc.CreatedAt
import eu.timepit.refined.api.Refined
import eu.timepit.refined.types.all.*
import org.scalacheck.Gen
import org.scalacheck.Gen.Choose

import java.time.Instant

object generators:
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

  def numericalNonNeg[T](using num: Numeric[T], c: Choose[T]): Gen[T] =
    Gen.oneOf[T](Gen.const[T](num.zero), Gen.posNum[T])

  val nonNegIntGen: Gen[NonNegInt] = numericalNonNeg[Int].map(NonNegInt.unsafeFrom)

  val posIntGen: Gen[PosInt] = Gen.posNum.map(PosInt.unsafeFrom)

  val nonNegDoubleGen: Gen[NonNegDouble] = numericalNonNeg[Double].map(NonNegDouble.unsafeFrom)

  val nonNegLongGen: Gen[NonNegLong] = numericalNonNeg[Long].map(NonNegLong.unsafeFrom)

  def nonNegDoubleGen[A](f: NonNegDouble => A): Gen[A] =
    nonNegDoubleGen.map(f)

  def sequenceListGen[A, B](list: List[A])(f: A => Gen[B]): Gen[List[B]] = Gen.sequence[List[B], B](
    list.map(item => f(item))
  )

  val instantGen: Gen[Instant] = Gen.oneOf(Gen.const(Instant.EPOCH), Gen.const(Instant.now()))

  val createdAtGen: Gen[CreatedAt] = instantGen.map(CreatedAt.apply)

  val refinedUriGen: Gen[RefinedUri] =
    for {
      scheme <- Gen.oneOf("https", "http")
      host   <- Gen.oneOf("example.com")
      path   <- Gen.option(Gen.alphaStr).map(_.getOrElse(""))
      raw = s"$scheme://$host/$path"
    } yield Refined.unsafeApply(raw)
end generators
