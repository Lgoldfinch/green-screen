package com.green.screen.ai.algebras.clients

import cats.data.NonEmptyVector
import cats.effect.*
import cats.syntax.all.*
import com.green.screen.ai.domain.perplexity.*
import com.green.screen.ai.generators.perplexity.*
import munit.{ CatsEffectSuite, ScalaCheckEffectSuite }
import org.http4s.*
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.client.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.scalacheck.Gen
import org.scalacheck.effect.PropF.forAllF
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.noop.NoOpLogger

class PerplexityClientSuite extends CatsEffectSuite with ScalaCheckEffectSuite {

  implicit val logger: SelfAwareStructuredLogger[IO] = NoOpLogger[IO]

  def routes(mkResponse: IO[Response[IO]]): HttpApp[IO] = {
    HttpRoutes
      .of[IO] { case POST -> root / _ / "chat" / "completions" =>
        mkResponse
      }
      .orNotFound
  }

  test("Should return an error response if the last message to Perplexity does not have role 'user'") {
    forAllF(
      perplexityConfigGen,
      Gen.listOf(perplexityMessageGen),
      perplexityMessageGen.suchThat(_.role =!= PerplexityRole.User)
    ) { case (config, perplexityMessages, perplexityMessage) =>
      val client = Client.fromHttpApp(routes(Ok()))

      val perplexityClient: PerplexityClient[IO] = PerplexityClient.make[IO](client, config)

      val messages = NonEmptyVector.fromVectorUnsafe(perplexityMessages.appended(perplexityMessage).toVector)

      perplexityClient.chat(messages).attempt.map {
        case Left(_: PerplexityRoleException) => assert(true)
        case Left(exception) =>
          fail(s"Should have failed with PerplexityRoleException. Failed with ${exception.getMessage}")
        case Right(value) => fail(s"This should have failed. Succeeded with ${value.toString}")
      }
    }
  }

  test("Should handle a bad request response gracefully") {
    forAllF(
      perplexityConfigGen,
      Gen.listOf(perplexityMessageGen),
      perplexityMessageGen.suchThat(_.role === PerplexityRole.User),
      perplexityErrorResponseGen
    ) { case (config, perplexityMessages, perplexityMessage, perplexityErrorResponse) =>
      val client = Client.fromHttpApp(routes(BadRequest(perplexityErrorResponse)))

      val perplexityClient: PerplexityClient[IO] = PerplexityClient.make[IO](client, config)

      val messages = NonEmptyVector.fromVectorUnsafe(perplexityMessages.appended(perplexityMessage).toVector)

      perplexityClient.chat(messages).attempt.map {
        case Left(err: PerplexityErrorResponse) =>
          assertEquals(err.getMessage, s"Request to Perplexity failed, got: ${err.error}")
        case Left(exception) =>
          fail(s"Should have failed with PerplexityRoleException. Failed with ${exception.getMessage}")
        case Right(value) => fail(s"This should have failed. Succeeded with ${value.toString}")
      }
    }
  }
//
//  test("Should handle ") {
//    forAllF(
//      perplexityConfigGen,
//      Gen.listOf(perplexityMessageGen),
//      perplexityMessageGen.suchThat(_.role === PerplexityRole.User),
//      perplexityErrorResponseGen
//    ) { case (config, perplexityMessages, perplexityMessage, perplexityErrorResponse) =>
//      val client = Client.fromHttpApp(routes( BadRequest(perplexityErrorResponse)))
//
//      val perplexityClient: PerplexityClient[IO] = PerplexityClient.make[IO](client, config)
//
//      val messages = perplexityMessages.appended(perplexityMessage)
//
//      perplexityClient.chat(messages).attempt.map {
//        case Left(err: PerplexityErrorResponse) =>
//          assertEquals(err.getMessage, s"Request to Perplexity failed, got: ${err.error}")
//        case Left(exception) =>
//          fail(s"Should have failed with PerplexityRoleException. Failed with ${exception.getMessage}")
//        case Right(value) => fail(s"This should have failed. Succeeded with ${value.toString}")
//      }
//    }
//  }

  test("Should return response body successfully otherwise") {
    forAllF(
      perplexityConfigGen,
      Gen.listOf(perplexityMessageGen),
      perplexityMessageGen.suchThat(_.role === PerplexityRole.User),
      perplexityResponseGen
    ) { case (config, perplexityMessages, perplexityMessage, perplexityResponse) =>
      val routes = HttpRoutes
        .of[IO] { case _ =>
          Ok(perplexityResponse)
        }
        .orNotFound
      /// .pure(Ok(perplexityResponse)).orNotFound

      val client = Client.fromHttpApp(routes)

      val perplexityClient: PerplexityClient[IO] = PerplexityClient.make[IO](client, config)

      val messages = NonEmptyVector.fromVectorUnsafe(perplexityMessages.appended(perplexityMessage).toVector)

      val result = perplexityClient.chat(messages)

      result.assertEquals(
        perplexityResponse
      )
    }
  }
}
