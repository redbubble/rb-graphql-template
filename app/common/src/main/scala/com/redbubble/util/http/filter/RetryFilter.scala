package com.redbubble.util.http.filter

import com.twitter.finagle.Service
import com.twitter.finagle.param.HighResTimer
import com.twitter.finagle.service.{RetryFilter => TwitterRetryFilter}
import com.twitter.util.{Duration, Try}

object RetryFilter {
  type RetryCondition[Req, Resp] = PartialFunction[(Req, Try[Resp]), Boolean]

  private val timer = HighResTimer.Default

  /**
    * Creates a filter that retries requests to an underlying `service`.
    *
    * @param service        The service to retry.
    * @param retries        A stream of durations, where each duration represents a retry attempt, e.g. a stream containing
    *                       three durations would retry three times, once for each duration.
    * @param retryCondition When to retry a request.
    */
  final def retry[Req, Resp](service: Service[Req, Resp],
      retries: Stream[Duration], retryCondition: RetryCondition[Req, Resp]): Service[Req, Resp] = {
    val retryFilter = TwitterRetryFilter(retries)(retryCondition)(timer)
    retryFilter.andThen(service)
  }
}
