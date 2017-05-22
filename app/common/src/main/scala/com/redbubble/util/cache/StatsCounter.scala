package com.redbubble.util.cache

import com.github.benmanes.caffeine.cache.stats.{CacheStats, StatsCounter => CaffeineStatsCounter}
import com.redbubble.util.metrics.StatsReceiver

//noinspection ScalaStyle
final class StatsCounter(cacheId: String, statsReceiver: StatsReceiver) extends CaffeineStatsCounter {
  private var hitCount: Long = 0
  private var missCount: Long = 0
  private var loadSuccessCount: Long = 0
  private var loadFailureCount: Long = 0
  private var totalLoadTime: Long = 0
  private var evictionCount: Long = 0
  private var evictionWeight: Long = 0

  private val stats = statsReceiver.scope(cacheId)
  private val hitsCounter = stats.counter("hits")
  private val missesCounter = stats.counter("misses")
  private val evictionsCounter = stats.counter("evictions")
  private val evictionsWeightCounter = stats.counter("evictions_weight")
  private val loadsSuccessCounter = stats.counter("loads", "success")
  private val loadFailureCounter = stats.counter("loads", "failure")
  private val loadTimeStat = stats.stat("load_time")

  stats.provideGauge("hit_miss_ratio")(hitCount.toFloat / (hitCount.toFloat + missCount.toFloat))
  stats.provideGauge("miss_hit_ratio")(missCount.toFloat / (hitCount.toFloat + missCount.toFloat))

  override def snapshot(): CacheStats =
    new CacheStats(
      hitCount, missCount, loadSuccessCount, loadFailureCount, totalLoadTime, evictionCount, evictionWeight
    )

  override def recordHits(count: Int): Unit = {
    hitCount = hitCount + count
    hitsCounter.incr(count)
  }

  override def recordMisses(count: Int): Unit = {
    missCount = missCount + count
    missesCounter.incr(count)
  }

  override def recordEviction(): Unit = recordEviction(1)

  override def recordEviction(weight: Int): Unit = {
    evictionCount = evictionCount + 1
    evictionWeight = evictionWeight + weight
    evictionsCounter.incr()
    evictionsWeightCounter.incr(weight)
  }

  override def recordLoadSuccess(loadTime: Long): Unit = {
    loadSuccessCount = loadSuccessCount + 1
    totalLoadTime = totalLoadTime + loadTime
    loadsSuccessCounter.incr()
    loadTimeStat.add(loadTime.toFloat)
  }

  override def recordLoadFailure(loadTime: Long): Unit = {
    loadFailureCount = loadFailureCount + 1
    totalLoadTime = totalLoadTime + loadTime
    loadFailureCounter.incr()
    loadTimeStat.add(loadTime.toFloat)
  }
}
