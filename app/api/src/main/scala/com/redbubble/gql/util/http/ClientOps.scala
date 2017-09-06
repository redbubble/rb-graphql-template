package com.redbubble.gql.util.http

import com.redbubble.util.http.ResponseClassifier.timeoutsRetryable
import com.twitter.conversions.time._
import com.twitter.finagle.Http.Client
import com.twitter.finagle.http.service.HttpResponseClassifier.ServerErrorsAsFailures
import com.twitter.finagle.service.{Backoff, RetryBudget, ResponseClassifier => FinagleResponseClassifier}

/**
  * Configures a HTTP client for our use within the Redbubble GraphQL Template API.
  *
  * For more information:
  * - Pooling - https://twitter.github.io/finagle/guide/Clients.html#pooling
  * - Retries - https://twitter.github.io/finagle/guide/Clients.html#retries
  * - Timeouts - https://twitter.github.io/finagle/guide/Clients.html#timeouts-expiration
  *
  * Note that we also set a top-level timeout for incoming requests to the server (i.e. the maximum amount of time the
  * server is allowed to spend handling the incoming request), so all these values here must fit inside that.
  * See [[com.redbubble.gql.util.config.Config.requestTimeout]] for details.
  */
trait ClientOps {
  /**
    * Defaults for a client connection, including timeouts & retries:
    * - We set up a jittered exponential backoff policy & cap the number of retries that we do. We don't try too hard
    * to have connections succeed.
    * - We treat 500s as failures (Finagle only handles transport errors by default).
    */
  final def configureDefaults(client: Client): Client =
    connectionPoolAndTimeoutConfig(client)
        // Note. this shouldn't need to be here but it causes failure accrual to kick in on valid retries
        .withSessionQualifier.noFailFast
        .withRetryBudget(RetryBudget(ttl = 10.seconds, minRetriesPerSec = 5, percentCanRetry = 0.2))
        .withRetryBackoff(Backoff.equalJittered(100.milliseconds, 10.seconds).take(10))
        .withResponseClassifier(timeoutsRetryable.orElse(ServerErrorsAsFailures).orElse(FinagleResponseClassifier.Default))

  /**
    * We set some basic config for client connections here. In particular:
    * - We disable fail fast, as we are currently only talking to a single server (or LB), see
    * http://twitter.github.io/finagle/guide/Clients.html#client-fail-fast for more details.
    * - We set some sensible (think so?) defaults for the nummber of connections, and waiters.
    * - Knowing that we'll have users on the end of a connection, and given we are server->server, we set
    * some low thresholds on acquisition and total request times. We assume the retries take care of intermittent
    * load on these downstream services.
    * - We up the TCP connection timeout to 2 seconds, to cater for a delay in acquiring connections to the monolith
    * (but leave the total acquisition time at higher, as this includes acquiring a session).
    */
  final def connectionPoolAndTimeoutConfig(client: Client): Client =
    client.withSessionPool.maxSize(50)
        .withSessionPool.maxWaiters(1000)
        .withTransport.connectTimeout(2.seconds)
        .withSession.acquisitionTimeout(5.seconds)
        .withRequestTimeout(30.seconds)
        //.withSessionQualifier.noFailFast
}

object ClientOps extends ClientOps
