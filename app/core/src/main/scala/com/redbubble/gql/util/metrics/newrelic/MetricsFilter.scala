package com.redbubble.gql.util.metrics.newrelic

import com.codahale.metrics.{Metric, MetricFilter}

/**
  * Removes all metrics we don't want to send to NR:
  * - Zipkin - don't currently use this.
  * - JVM - New Relic gives us these already.
  */
final class MetricsFilter extends MetricFilter {
  private val ignoredPrefixes = Seq(
    "clnt.zipkin-tracer",
    "jvm"
  )

  override def matches(name: String, metric: Metric) = ignoredPrefixes.exists(!name.startsWith(_))
}
