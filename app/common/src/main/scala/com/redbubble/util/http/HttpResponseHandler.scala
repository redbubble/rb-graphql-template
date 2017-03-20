package com.redbubble.util.http

import com.redbubble.util.http.Errors._
import com.twitter.finagle.http.Response
import com.twitter.util.Future
import featherbed.request.InvalidResponse
import io.circe.Decoder

trait HttpResponseHandler { self: JsonApiClient =>
  private val stats = statsReceiver.scope("client_downstream")
  private val errorCounter = stats.counter(metricsId, "error")
  private val successCounter = stats.counter(metricsId, "success")
  private val totalCounter = stats.counter(metricsId, "total")

  /**
    * Handles a valid, expected response, promoting the failure into the type system (i.e. `Either` rather than a failing
    * `Future`).
    *
    * By an "expected" response, we mean a successful or failing response in the format we expect (i.e. we have a
    * `Decoder` for the format).
    */
  protected final def handleValidResponse[R: Decoder](
      responseTuple: (Either[Throwable, R], Response), interaction: ServiceInteraction): (Either[ApiError, R], Response) = {
    totalCounter.incr()
    responseTuple match {
      case (Left(error), response) =>
        logError(error, interaction)
        (Left(downstreamError(error, interaction)), response)
      case (Right(r), response) =>
        logSuccess(interaction)
        (Right(r), response)
    }
  }

  /**
    * Handles an invalid or unexpected response, promoting the failure into the type system (i.e. `Either` rather than a failing
    * `Future`).
    *
    * By an invalid, we mean a successful or failing response in a format that we do not expect (i.e. we do not have a
    * `Decoder` for the format).
    */
  protected final def handleInvalidResponse[R](
      request: HttpRequest): PartialFunction[Throwable, Future[(Either[ApiError, R], Response)]] = {
    case ir @ InvalidResponse(_, _) =>
      val interaction = ServiceInteraction(request, Some(HttpResponse(ir.response)))
      logError(ir, interaction)
      Future.value((Left(downstreamError(ir, interaction)), ir.response))
  }

  private def logSuccess(interaction: ServiceInteraction): Unit = {
    successCounter.incr()
    logger.trace(s"Success response: ${interaction.responseBody}")
  }

  private final def logError(error: Throwable, interaction: ServiceInteraction): Unit = {
    errorCounter.incr()
    logger.warn(s"Error making request to '${interaction.request.url}': $error")
    logger.trace(s"Error response: ${interaction.responseBody}")
  }
}
