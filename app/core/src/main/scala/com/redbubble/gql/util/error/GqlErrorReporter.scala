package com.redbubble.gql.util.error

import com.redbubble.gql.util.config.Config.rollbarAccessKey
import com.redbubble.gql.util.config.Environment.env
import com.redbubble.gql.util.config.{Environment, Production}
import com.redbubble.gql.util.async.futurePool
import com.redbubble.util.config.Environment
import com.redbubble.util.error._
import com.redbubble.util.http.DownstreamError
import com.twitter.finagle.{ChannelClosedException, IndividualRequestTimeoutException}
import com.twitter.finagle.http.Status.InternalServerError
import com.twitter.util.FuturePool

private final class GqlErrorReporter(
    environment: Environment, enabledEnvironments: Seq[Environment])(implicit fp: FuturePool) extends ErrorReporter {
  private val underlying = new RollbarErrorReporter(rollbarAccessKey, environment, enabledEnvironments)(fp)

  override def registerForUnhandledExceptions() = underlying.registerForUnhandledExceptions()

  def report(level: ErrorLevel, t: Throwable, extraData: Option[ExtraData] = None): Unit =
    reportLevel(t).foreach { level =>
      val extra = errorExtraData(t).map(eed => eed ++ extraData.getOrElse(Map()))
      underlying.report(level, t, extra)
    }

  private def reportLevel(t: Throwable): Option[ErrorLevel] = t match {
    // An example of not logging a 404 from a downstream service.
    case e @ DownstreamError(_, i) if e.getMessage.contains("Resource Not Found") && i.requestUrl.startsWith(env.peopleApiUrl.toString) =>
      Some(Debug)
    // We don't care about individual request timeouts to downstream services, as these are usually intermittent.
    case _: IndividualRequestTimeoutException =>
      Some(Debug)
    // We don't care about downstream services closing connections, as these are usually intermittent.
    case _: ChannelClosedException =>
      Some(Debug)
    case _ => Some(Error)
  }

  private val DownstreamPrefix = "downstream.service"

  private def errorExtraData(t: Throwable): Option[ExtraData] = t match {
    case e: DownstreamError => Some(
      Map(
        s"$DownstreamPrefix.request.url" -> e.interaction.requestUrl,
        s"$DownstreamPrefix.request.headers" -> e.interaction.requestHeaders,
        s"$DownstreamPrefix.request.body" -> e.interaction.requestBody,
        s"$DownstreamPrefix.response.status" -> e.interaction.responseStatusCode,
        s"$DownstreamPrefix.response.body" -> e.interaction.responseBody
      )
    )
    case _ => None
  }
}

/**
  * Entry point to error reporting. Knows about which errors to log vs. not log.
  */
object GqlErrorReporter {
  val errorReporter: ErrorReporter = new GqlErrorReporter(Environment.env, Seq(Production))(futurePool)
}
