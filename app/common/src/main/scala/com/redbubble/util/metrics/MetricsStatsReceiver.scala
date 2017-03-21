/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Rodrigo Lazoti
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.redbubble.util.metrics

import com.codahale.metrics.{Gauge => DwGauge, Metric => DwMetric, MetricFilter => DwMetricFilter, MetricRegistry => DwMetricsRegistry}
import com.redbubble.util.http.Errors
import com.redbubble.util.http.Errors.error
import com.redbubble.util.metrics.MetricsStatsReceiver._
import com.twitter.finagle.stats.{Counter => FinagleCounter, Gauge => FinagleGauge, Stat => FinagleStat, StatsReceiver => FinagleStatsReceiver}

import scala.collection.JavaConverters._

/**
  * A `com.twitter.finagle.stats.StatsReceiver` instance that bridges Finagle Stats' metrics to Dropwizard Metrics.
  *
  * This classes essentially configures itself as a `StatsReceiver`, and sends all metrics to Dropwizard Metrics,
  * which in turn are sent to New Relic (using `com.redbubble.gql.util.metrics.newrelic.`).
  *
  * See `com.twitter.finagle.stats.DefaultStatsReceiver` for details on how this is done at runtime (basically, it
  * loads classes defined in `resources/META-INF/services`).
  *
  * Original implementation taken from Finagle Metrics: https://github.com/rlazoti/finagle-metrics.
  */
final class MetricsStatsReceiver extends FinagleStatsReceiver {
  override val repr = this

  override def counter(names: String*) = new FinagleCounter {
    private val meter = logMetric(names)(ns => metrics.meter(formatMetricNames(ns)))

    override def incr(delta: Int) = meter.mark(delta.toLong)
  }

  override def stat(names: String*) = new FinagleStat {
    private val histogram = logMetric(names)(ns => metrics.histogram(formatMetricNames(ns)))

    override def add(value: Float) = histogram.update(value.toLong)
  }

  override def addGauge(names: String*)(f: => Float) = new FinagleGauge {
    logMetric(names) { ns =>
      val name = formatMetricNames(ns)

      // we remove old gauge's value before adding a new one
      metrics.getGauges(new DwMetricFilter() {
        override def matches(metricName: String, metric: DwMetric) = metricName == name
      }).asScala.foreach(entry => metrics.remove(entry._1))

      metrics.register(name, new DwGauge[Float]() {
        override def getValue: Float = f
      })
    }

    // Note. We want to remove both regular and deduped names in case we've deduplicated this metric name (of which we
    // have no way, here, of knowing).
    override def remove() = {
      metrics.remove(formatMetricNames(names))
      // TODO TJA Fix this to remove up to MaxDedupes.
      metrics.remove(formatMetricNames(names :+ deduplicatedName))
      ()
    }
  }
}

import com.redbubble.util.log.UtilsLogger.log

object MetricsStatsReceiver {
  private lazy val duplicateMetricCounter = metrics.counter("duplicate_metric")
  val MaxDedupes: Int = 5
  val deduplicatedName: String = "deduped"
  lazy val metrics: DwMetricsRegistry = new DwMetricsRegistry

  /**
    * Log a metric, using the given `metricF` function, handling duplicate metric names if needed.
    *
    * Dropwizard Metrics doesn't like duplicate names, while Finagle will use the same name for stats & counters
    * around the same metric.
    */
  final def logMetric[M](names: Seq[String], dedupeCount: Int = 0)(metricF: (Seq[String] => M)): M =
    try {
      metricF(names)
    } catch {
      // This exception is generated from com.codahale.metrics.MetricRegistry#register
      case e: IllegalArgumentException if e.getMessage.startsWith("A metric named") =>
        duplicateMetricCounter.inc()
        log.debug(s"Duplicate metric name: '${formatMetricNames(names)}', deduplicating: ${e.getMessage}")
        // Note. We risk blowing the stack here with this recursion, so we cap it.
        if (dedupeCount < MaxDedupes) {
          logMetric(names :+ deduplicatedName, dedupeCount + 1)(metricF)
        } else {
          throw error(s"Exceeded $dedupeCount metric deduplications for ${formatMetricNames(names)}", e)
        }
    }

  def formatMetricNames(names: Seq[String]): String = names.mkString(".")
}
