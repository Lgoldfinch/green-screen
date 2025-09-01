package com.green.screen.banking

import cats.effect.*
import com.green.screen.common.PostgresSuite
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.noop.NoOpLogger
import skunk.{ Command, * }
import skunk.implicits.*

trait BankingPostgresSuite extends PostgresSuite {

  given SelfAwareStructuredLogger[IO] = NoOpLogger[IO]

  override val flushTables: List[Command[Void]] =
    List("companies", "transactions", "users").map { table =>
      sql"DELETE FROM #$table".command
    }

}
