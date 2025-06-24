package com.green.screen.analytics.engine.generators

import com.green.screen.analytics.engine.config.config.{ ApiKey, PerplexityBaseUri, PerplexityConfig }
import com.green.screen.analytics.engine.domain.perplexity.{
  CompletionId,
  FinishReason,
  ModelName,
  PerplexityChoice,
  PerplexityErrorDetail,
  PerplexityErrorResponse,
  PerplexityMessage,
  PerplexityResponse,
  PerplexityRole,
  PerplexityUsage
}
import com.green.screen.analytics.engine.generators.nonEmptyStringGen
import org.scalacheck.Gen

object perplexity {

  val apiKeyGen: Gen[ApiKey] =
    nonEmptyStringGen.map(ApiKey.apply)

  val baseUriGen: Gen[PerplexityBaseUri] =
    refinedUriGen.map(PerplexityBaseUri.apply)

  val perplexityConfigGen: Gen[PerplexityConfig] = for {
    apiKey  <- apiKeyGen
    baseUri <- baseUriGen
    model   <- nonEmptyStringGen(identity)
  } yield PerplexityConfig(apiKey, baseUri, model)

  val completionIdGen: Gen[CompletionId] = nonEmptyStringGen(CompletionId.apply)

  val modelNameGen: Gen[ModelName] = nonEmptyStringGen(ModelName.apply)

  val finishReasonGen: Gen[FinishReason] = nonEmptyStringGen(FinishReason.apply)

  val perplexityUsage: Gen[PerplexityUsage] = for {
    promptTokens     <- nonNegIntGen
    completionTokens <- nonNegIntGen
    totalTokens      <- posIntGen
  } yield PerplexityUsage(promptTokens, completionTokens, totalTokens)

  val perplexityRoleGen: Gen[PerplexityRole] = Gen.oneOf(PerplexityRole.values.toList)

  val perplexityUsageGen: Gen[PerplexityRole] = Gen.oneOf(PerplexityRole.values.toList)

  val perplexityMessageGen: Gen[PerplexityMessage] = for {
    role    <- perplexityRoleGen
    content <- nonEmptyStringGen(identity)
  } yield PerplexityMessage(role, content)

  val perplexityChoiceGen: Gen[PerplexityChoice] = for {
    index             <- nonNegIntGen
    perplexityMessage <- perplexityMessageGen
    finishReason      <- Gen.option(finishReasonGen)
  } yield PerplexityChoice(index, perplexityMessage, finishReason)

  val perplexityResponseGen: Gen[PerplexityResponse] = for {
    completionId <- completionIdGen
    obj          <- nonEmptyStringGen(identity)
    createdAt    <- nonNegLongGen
    choices   <- Gen.listOf(perplexityChoiceGen) // Not logical for this to potentially be an empty list. Monitor this.
    modelName <- modelNameGen
    usage     <- Gen.option(perplexityUsage)
  } yield PerplexityResponse(completionId, obj, createdAt, modelName, choices, usage)

  val perplexityErrorResponseGen: Gen[PerplexityErrorResponse] = for {
    message   <- Gen.alphaStr
    errorType <- Gen.alphaStr
    errorDetail = PerplexityErrorDetail(message, errorType)
  } yield PerplexityErrorResponse(errorDetail)
}
