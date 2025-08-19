package com.green.screen.common

import cats.effect.*
import cats.syntax.all.*
import natchez.Trace.Implicits.noop
import skunk.*

trait PostgresSuite extends ResourceSuite {

  val flushTables: List[Command[Void]]

  type Res = Resource[IO, Session[IO]]

  override def sharedResource: Resource[IO, Res] =
    Session
      .pooled[IO](
        host = "localhost",
        port = 5432,
        user = "postgres",
        database = "green-screen-postgres",
        password = Some("password"),
        max = 16
      )
      .beforeAll {
        _.use { s =>
          flushTables.traverse_(s.execute)
        }
      }

}
