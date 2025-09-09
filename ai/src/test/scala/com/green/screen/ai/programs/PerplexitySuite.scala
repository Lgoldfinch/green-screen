package com.green.screen.ai.programs

import cats.Id
import cats.data.NonEmptyVector
import com.green.screen.ai.algebras.clients.PerplexityClient
import com.green.screen.ai.domain.perplexity.{PerplexityMessage, PerplexityResponse}
import com.green.screen.ai.generators.common.aiAskRequestGen
import com.green.screen.ai.generators.perplexity.perplexityResponseGen
import com.green.screen.common.GivenInstances.given
import munit.{FunSuite, ScalaCheckSuite}
import org.scalacheck.Prop.forAll
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.noop.NoOpLogger

class PerplexitySuite extends ScalaCheckSuite {

  given Logger[Id] = NoOpLogger[Id]

  private def perplexityClient(perplexityResponse: PerplexityResponse) = new PerplexityClient[Id] {
    override def chat(
        messages: NonEmptyVector[PerplexityMessage]
    ): Id[PerplexityResponse] = perplexityResponse
  }

// This will have more business logic when I start getting some requirements
  test("Should return whatever the Perplexity client returns") {
    forAll(perplexityResponseGen, aiAskRequestGen) { case (perplexityResponse, aiAskRequest) =>
      val client  = perplexityClient(perplexityResponse)
      val program = new Perplexity[Id](client)

      val response = program.run(aiAskRequest)

      assertEquals(response, perplexityResponse)
    }
  }
}
