package com.redbubble.gql.metrics.newrelic

import java.util.concurrent.TimeUnit

import com.palominolabs.metrics.newrelic.NewRelicReporter
import com.redbubble.util.log.UtilsLogger.log
import com.redbubble.util.metrics.MetricsStatsReceiver
import com.twitter.app.App

/**
  * Send metrics to New Relic.
  *
  * Metrics are sent to New Relic by using:
  * - Bridge Finagle stats to Dropwizard metrics (see `com.redbubble.util.metrics.MetricsStatsReceiver`.).
  * - Send Dropwizard metrics to New Relic: https://github.com/palominolabs/metrics-new-relic
  */
trait NewRelicMetrics { app: App =>
  private val reporterName = "New Relic Metrics Reporter"
  private final lazy val registry = MetricsStatsReceiver.metrics
  private final lazy val reporter = NewRelicReporter.forRegistry(registry)
      .name(reporterName)
      .filter(new MetricsFilter)
      .attributeFilter(new MetricsAttributeFilter)
      .rateUnit(TimeUnit.SECONDS)
      .durationUnit(TimeUnit.MILLISECONDS)
      .metricNamePrefix("")
      .build()

  premain {
    log.info("Starting to report metrics to New Relic agent")
    reporter.start(60, TimeUnit.SECONDS)
  }

  onExit {
    reporter.report()
    reporter.stop()
  }
}
