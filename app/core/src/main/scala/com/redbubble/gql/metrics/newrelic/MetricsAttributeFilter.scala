package com.redbubble.gql.metrics.newrelic

import com.codahale.metrics.{Gauge, Histogram, Meter}
import com.palominolabs.metrics.newrelic.AllDisabledMetricAttributeFilter

/**
  * Disable all metrics, except those that we want to enable explicitly.
  *
  * Note that we only care about those metric types that we explicitly record via `MetricsStatsReceiver` (meter,
  * histogram & gauge), and those that we don't get in New Relic for free already, e.g. min/max.
  */
final class MetricsAttributeFilter extends AllDisabledMetricAttributeFilter {
  // Meter (Finagle counter)
  override def recordMeter1MinuteRate(name: String, metric: Meter) = true

  override def recordMeter5MinuteRate(name: String, metric: Meter) = true

  override def recordMeter15MinuteRate(name: String, metric: Meter) = true

  // Gauge
  override def recordGaugeValue(name: String, metric: Gauge[_]) = true

  // Histogram (Finagle stat)
  override def recordHistogram75thPercentile(name: String, metric: Histogram) = true

  override def recordHistogram95thPercentile(name: String, metric: Histogram) = true

  override def recordHistogram98thPercentile(name: String, metric: Histogram) = true

  override def recordHistogram99thPercentile(name: String, metric: Histogram) = true

  override def recordHistogram999thPercentile(name: String, metric: Histogram) = true

  override def recordHistogramStdDev(name: String, metric: Histogram) = true
}
