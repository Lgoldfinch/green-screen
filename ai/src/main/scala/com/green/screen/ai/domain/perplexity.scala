package com.green.screen.ai.domain

import cats.Eq
import cats.data.NonEmptyVector
import cats.syntax.all.*
import eu.timepit.refined.types.numeric.{ NonNegInt, PosInt }
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.derivation.*
import io.circe.derivation.Configuration.default
import io.circe.refined.*
import io.circe.{ Decoder, Encoder }

import scala.util.control.NoStackTrace

object perplexity {

  given configuration: Configuration = default.withSnakeCaseMemberNames.withTransformConstructorNames(_.toLowerCase)

  opaque type CompletionId = NonEmptyString

  object CompletionId {
    def apply(value: NonEmptyString): CompletionId = value

    extension (id: CompletionId) def value: NonEmptyString = id
  }

  opaque type ModelName = NonEmptyString

  object ModelName {
    def apply(value: NonEmptyString): ModelName = value

    extension (m: ModelName) def value: NonEmptyString = m
  }

  opaque type MessageContent = NonEmptyString

  object MessageContent {
    def apply(value: NonEmptyString): MessageContent = value

    extension (c: MessageContent) def value: NonEmptyString = c
  }

  opaque type FinishReason = NonEmptyString

  object FinishReason {
    def apply(value: NonEmptyString): FinishReason = value

    extension (r: FinishReason) def value: NonEmptyString = r
  }

  final case class PerplexityRequest(model: NonEmptyString, private val messages: NonEmptyVector[PerplexityMessage])
      derives ConfiguredCodec

  object PerplexityRequest {
    def buildRequest(
        model: NonEmptyString,
        messages: NonEmptyVector[PerplexityMessage]
    ): Either[PerplexityRoleException, PerplexityRequest] =
      if (messages.last.role =!= PerplexityRole.User)
        PerplexityRoleException(messages.last.role).asLeft[PerplexityRequest]
      else PerplexityRequest(model, messages).asRight[PerplexityRoleException]
  }

  sealed trait PerplexityException extends NoStackTrace
  case class PerplexityRoleException(perplexityRole: PerplexityRole) extends PerplexityException {
    override def getMessage: String = s"Last message must have role `user` or `tool`. Got $perplexityRole instead."
  }

  enum PerplexityRole derives ConfiguredEnumCodec {
    case Assistant
    case System
    case User
  }

  given eq: Eq[PerplexityRole] with {
    override def eqv(x: PerplexityRole, y: PerplexityRole): Boolean = x == y
  }

  final case class PerplexityMessage(
      role: PerplexityRole,
      content: NonEmptyString
  ) derives ConfiguredCodec

  final case class PerplexityChoice(
      index: NonNegInt,
      message: PerplexityMessage,
      finishReason: Option[FinishReason]
  ) derives ConfiguredCodec

  final case class PerplexityUsage(
      promptTokens: NonNegInt,
      completionTokens: NonNegInt,
      totalTokens: PosInt
  ) derives ConfiguredCodec

  final case class PerplexityResponse(
      id: CompletionId,
      `object`: NonEmptyString,
      model: ModelName,
      choices: List[PerplexityChoice],
      usage: Option[PerplexityUsage]
  ) derives ConfiguredCodec

  final case class PerplexityErrorResponse(error: PerplexityErrorDetail) extends NoStackTrace derives ConfiguredCodec {
    override def getMessage: String = s"Request to Perplexity failed, got: $error"
  }

  final case class PerplexityErrorDetail(
      message: String,
      `type`: String
  ) derives ConfiguredCodec
}
