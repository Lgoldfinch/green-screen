package com.green.screen.analytics.engine.algebras

import cats.effect.{ Concurrent, Resource }
import org.typelevel.log4cats.Logger
import skunk.Session
trait Algebras[F[_]]:
  val companies: Companies[F]
  val transactions: OpenAPITransactions[F]
  val users: Users[F]
  val userOpenApiData: UserOpenApiData[F]
end Algebras

object Algebras:
  def make[F[_]: Concurrent: Logger](postgres: Resource[F, Session[F]]): Algebras[F] =
    new Algebras[F] {
      override val companies: Companies[F]              = Companies.make[F](postgres)
      override val transactions: OpenAPITransactions[F] = OpenAPITransactions.make[F](postgres)
      override val users: Users[F]                      = Users.make[F](postgres)
      override val userOpenApiData: UserOpenApiData[F]  = UserOpenApiData.make[F](postgres)
    }
end Algebras
