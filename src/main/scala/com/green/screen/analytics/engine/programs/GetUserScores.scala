package com.green.screen.analytics.engine.programs

import com.green.screen.analytics.engine.algebras.Users
import com.green.screen.analytics.engine.domain.users.{ UserScore, UserUuid }

class GetUserScores[F[_]](users: Users[F]) {
  def getScores(userUuid: UserUuid): F[UserScore] = users.getScore(userUuid)
}
