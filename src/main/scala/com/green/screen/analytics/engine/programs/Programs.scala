package com.green.screen.analytics.engine.programs

import cats.MonadThrow
import com.green.screen.analytics.engine.algebras.Algebras
import com.green.screen.effects.GenUUID

trait Programs[F[_]]:
  val getUserScores: GetUserScores[F]
  val processTransaction: ProcessTransaction[F]
end Programs

object Programs {
  def make[F[_]: MonadThrow: GenUUID](algebras: Algebras[F]): Programs[F] = new Programs[F] {
    override val getUserScores: GetUserScores[F] = GetUserScores[F](algebras.users)
    override val processTransaction: ProcessTransaction[F] =
      ProcessTransaction[F](algebras.companies, algebras.transactions)
  }
}
