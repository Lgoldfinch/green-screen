package com.green.screen.analytics.engine

import cats.effect.*
import cats.implicits.*
import natchez.Trace.Implicits.noop
import org.typelevel.log4cats.SelfAwareStructuredLogger
import skunk.*
import skunk.implicits.*
import org.typelevel.log4cats.noop.NoOpLogger

trait PostgresSuite extends ResourceSuite {

  implicit val logger: SelfAwareStructuredLogger[IO] = NoOpLogger[IO]

  val flushTables: List[Command[Void]] =
    List("companies", "transactions", "users").map { table =>
      sql"DELETE FROM #$table".command
    }

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
