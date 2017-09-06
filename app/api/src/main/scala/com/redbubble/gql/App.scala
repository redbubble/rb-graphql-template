package com.redbubble.gql

import com.redbubble.gql.util.config.Config
import com.redbubble.gql.util.config.Environment.env
import com.redbubble.gql.util.error.GqlErrorReporter.errorReporter
import com.redbubble.gql.util.log.CoreLogger
import com.redbubble.gql.util.log.CoreLogger._
import com.redbubble.gql.util.metrics.newrelic.NewRelicMetrics
import com.redbubble.util.async.AsyncOps
import com.redbubble.util.error.ErrorReportingMonitor
import com.redbubble.util.log.Slf4jLogging
import com.twitter.app.{App => TwitterApp}
import com.twitter.finagle.{Http, ListeningServer}
import com.twitter.server._

/**
  * This trait defines the features of TwitterServer that we use, within the API.
  *
  * See this for more information: https://twitter.github.io/twitter-server/Features.html
  */
private[gql] trait ServiceFeatures extends TwitterApp
    with Hooks
    with Linters
    with Slf4jLogging
    with Stats
    with NewRelicMetrics
    with AdminHttpServer
    with Admin
    with Lifecycle

object ErrorMonitor extends ErrorReportingMonitor(CoreLogger.log, errorReporter)

trait BootableService { self: TwitterApp =>
  private val server = Http.server
      .withLabel(Config.systemId)
      .withStreaming(enabled = true)
      .withAdmissionControl.concurrencyLimit(Config.maxConcurrentRequests, Config.maxWaiters)
      .withRequestTimeout(Config.requestTimeout)
      .withMaxRequestSize(Config.maxRequestSize)
      .withMonitor(ErrorMonitor)
      .configured(Http.Netty4Impl)

  def boot(): ListeningServer = {
    errorReporter.registerForUnhandledExceptions()
    val booted = server.serve(Config.listenAddress, GqlApi.apiService)
    onExit {
      log.info(s"${Config.systemId} is shutting down...")
      booted.close()
      ()
    }
    booted
  }
}

object App extends BootableService with ServiceFeatures {
  def main(): Unit = {
    val server = boot()
    log.info(s"Booted ${Config.systemId} in ${env.name} mode on ${server.boundAddress}")
    AsyncOps.blockUnit(server)
  }
}
