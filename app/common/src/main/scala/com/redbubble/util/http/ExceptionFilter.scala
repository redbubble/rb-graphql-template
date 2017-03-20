package com.redbubble.util.http

import com.redbubble.util.metrics.StatsReceiver
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import io.finch.Encode

import scala.util.control.NonFatal

/**
  * Top-level exception handling filter. If an underlying service does not handle an exception it will be handled here.
  *
  * Copied from com.twitter.finagle.http.filter.ExceptionFilter
  *
  * @param encoder       A Finch encoder for turning exceptions into responses.
  * @param errorHandler  An error handler, that knows how to turn exceptions into `com.twitter.finagle.http.Response`s.
  * @param statsReceiver Where to send metrics to.
  */
abstract class ExceptionFilter(
    encoder: Encode.Json[Throwable],
    errorHandler: ErrorHandler,
    statsReceiver: StatsReceiver)
    extends SimpleFilter[Request, Response] {

  private val stats = statsReceiver.scope("unhandled_exception")
  private val errorCounter = stats.counter("error")

  def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    val finalResponse =
      try {
        service(request)
      } catch {
        case NonFatal(e) =>
          errorCounter.incr()
          Future.exception(e)
      }
    finalResponse.rescue(errorHandler.topLevelErrorHandler(request, encoder))
  }
}
