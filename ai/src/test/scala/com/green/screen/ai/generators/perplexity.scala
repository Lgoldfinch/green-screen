package com.green.screen.ai.generators

import com.green.screen.ai.domain.*
import com.green.screen.common.config.ApiKey
import com.green.screen.common.generators.*
import com.green.screen.ai.domain.perplexity.*
import org.scalacheck.Gen

object perplexity {

  val apiKeyGen: Gen[ApiKey] =
    nonEmptyStringGen(ApiKey.apply)

  val baseUriGen: Gen[PerplexityBaseUri] =
    refinedUriGen.map(PerplexityBaseUri.apply)

  val perplexityConfigGen: Gen[PerplexityConfig] = for {
    apiKey  <- apiKeyGen
    baseUri <- baseUriGen
    model   <- nonEmptyStringGen(identity)
  } yield PerplexityConfig(apiKey.value, baseUri, model)

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

  val perplexityChoiceGen: Gen[PerplexityChoice] =
    for {
      index             <- nonNegIntGen
      perplexityMessage <- perplexityMessageGen
      finishReason      <- Gen.option(finishReasonGen)
    } yield PerplexityChoice(index, perplexityMessage, finishReason)

  val perplexityResponseGen: Gen[PerplexityResponse] = for {
    completionId <- completionIdGen
    obj          <- nonEmptyStringGen(identity)
    modelName    <- modelNameGen
    choices <- Gen.listOf(perplexityChoiceGen) // Not logical for this to potentially be an empty list. Monitor this.
    usage   <- Gen.option(perplexityUsage)
  } yield PerplexityResponse(completionId, obj, modelName, choices, usage)

  val perplexityErrorResponseGen: Gen[PerplexityErrorResponse] = for {
    message   <- Gen.alphaStr
    errorType <- Gen.alphaStr
    errorDetail = PerplexityErrorDetail(message, errorType)
  } yield PerplexityErrorResponse(errorDetail)
}
