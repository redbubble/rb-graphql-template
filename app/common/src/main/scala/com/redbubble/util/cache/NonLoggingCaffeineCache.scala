package com.redbubble.util.cache

import com.github.benmanes.caffeine.cache.Cache

import scala.concurrent.duration.Duration
import scalacache.caffeine.CaffeineCache

final case class NonLoggingCaffeineCache(underlying: Cache[String, Object]) extends CaffeineCache(underlying) {
  override protected def logCacheHitOrMiss[A](key: String, result: Option[A]): Unit = {
  }

  override protected def logCachePut(key: String, ttl: Option[Duration]): Unit = {
  }
}
