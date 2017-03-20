package com.redbubble.util.cache

import java.util.concurrent.Executors._

import com.redbubble.util.metrics.StatsReceiver
import com.redbubble.util.spec.SpecHelper
import com.twitter.conversions.time._
import com.twitter.util.Future
import org.specs2.mutable.Specification

final class MemoryCacheSpec extends Specification with SpecHelper {
  private lazy val statsReceiver = StatsReceiver.stats

  "An in-memory cache" >> {
    "caches things once only" >> {
      //noinspection ScalaStyle
      var executed = 0
      //inspection ScalaStyle

      val mutate = Future {
        executed = executed + 1
      }

      val cache = MemoryCache.newCache("test-cache", 100, 5.minutes)(newSingleThreadExecutor, statsReceiver)
      cache.caching[Unit](CacheKey("some-key"))(mutate)
      cache.caching[Unit](CacheKey("some-key"))(mutate)
      cache.caching[Unit](CacheKey("some-key"))(mutate)
      executed must beEqualTo(1)
    }
  }
}
