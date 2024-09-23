package com.green.screen.analytics.engine

import cats.data.NonEmptyList
import eu.timepit.refined.types.string.NonEmptyString
import org.scalacheck.Gen

package object generators:
  private val nonEmptyStringGen: Gen[NonEmptyString] = Gen.alphaStr.flatMap(NonEmptyString.unsafeFrom)
  def nonEmptyStringGen[A](f: NonEmptyString => A): Gen[A] = nonEmptyStringGen.map(f)
  
  def nelGen[A](a: Gen[A]): Gen[NonEmptyList[A]] = for {
    head <- a
    tail <- Gen.listOf(a)
  } yield NonEmptyList.of(head, tail*)
end generators

