package com.redbubble.util.metrics

import com.twitter.finagle.stats.{StatsReceiver => FinagleStatsReceiver, _}

/**
  * An interface for recording metrics.
  *
  * Wraps a `com.twitter.finagle.stats.StatsReceiver` to provide a more restricted interface than the Finagle version,
  * scoped only to what we need.
  *
  * Do not access this directly, instead use `com.redbubble.gql.util.metrics.Metrics` to obtain an appropriate isntance.
  */
trait StatsReceiver {
  def scope(namespaces: String*): StatsReceiver

  def counter(name: String*): Counter

  def stat(name: String*): Stat

  def addGauge(name: String*)(f: => Float): Gauge

  def provideGauge(name: String*)(f: => Float): Unit
}

private final class StatsReceiver_(finagleStats: FinagleStatsReceiver) extends StatsReceiver {
  override def scope(namespaces: String*): StatsReceiver = new StatsReceiver_(finagleStats.scope(namespaces: _*))

  override def counter(name: String*): Counter = finagleStats.counter(name: _*)

  override def stat(name: String*): Stat = finagleStats.stat(name: _*)

  override def addGauge(name: String*)(f: => Float): Gauge = finagleStats.addGauge(name: _*)(f)

  override def provideGauge(name: String*)(f: => Float) = finagleStats.provideGauge(name: _*)(f)
}

object StatsReceiver {
  lazy val stats: StatsReceiver = new StatsReceiver_(DefaultStatsReceiver.get)
}
