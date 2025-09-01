package com.green.screen.common

import cats.effect.IO
import cats.effect.kernel.Resource
import cats.syntax.all.*
import weaver.scalacheck.Checkers
import weaver.{ Expectations, IOSuite }

abstract class ResourceSuite extends IOSuite with Checkers {
  implicit class SharedResOps(res: Resource[IO, Res]) {
    def beforeAll(f: Res => IO[Unit]): Resource[IO, Res] =
      res.evalTap(f)

    def afterAll(f: Res => IO[Unit]): Resource[IO, Res] =
      res.flatTap(x => Resource.make(IO.unit)(_ => f(x)))
  }

  def testBeforeAfterEach(
      before: Res => IO[Unit],
      after: Res => IO[Unit]
  ): String => (Res => IO[Expectations]) => Unit =
    name => fa => test(name)(res => before(res) >> fa(res).guarantee(after(res)))

  def testAfterEach(
      after: Res => IO[Unit]
  ): String => (Res => IO[Expectations]) => Unit =
    testBeforeAfterEach(_ => IO.unit, after)

  def testBeforeEach(
      before: Res => IO[Unit]
  ): String => (Res => IO[Expectations]) => Unit =
    testBeforeAfterEach(before, _ => IO.unit)

}
