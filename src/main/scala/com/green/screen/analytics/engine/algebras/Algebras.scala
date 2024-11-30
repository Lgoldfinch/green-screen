package com.green.screen.analytics.engine.algebras

import cats.effect.{Concurrent, Resource}
import org.typelevel.log4cats.Logger
import skunk.Session
trait Algebras[F[_]]:
  val companies: Companies[F]
  val transactions: UserTransactions[F]
end Algebras

object Algebras:
  def make[F[_]: Concurrent: Logger](postgres: Resource[F, Session[F]]): Algebras[F] =
    new Algebras[F] {
      override val companies: Companies[F] = Companies.make[F](postgres)
      override val transactions: UserTransactions[F] = UserTransactions.make[F](postgres)
    }
end Algebras

