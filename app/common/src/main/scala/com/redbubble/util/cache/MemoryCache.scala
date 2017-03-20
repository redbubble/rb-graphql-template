package com.redbubble.util.cache

import java.util.concurrent.{Executor, TimeUnit}

import com.redbubble.util.async.syntax._
import com.redbubble.util.cache.CacheOps.sanitiseCacheName
import com.redbubble.util.metrics.StatsReceiver
import com.twitter.util.{Duration, Future}

import scala.concurrent.ExecutionContext.fromExecutor
import scala.concurrent.duration.{Duration => ScalaDuration}
import scalacache.serialization.{Codec, InMemoryRepr}
import scalacache.{Flags, ScalaCache}

sealed trait MemoryCache {
  /**
    * Cache the result of executing `f` using the given `key`.
    */
  def caching[V](key: CacheKey)(f: => Future[V]): Future[V]

  /**
    * Manually put a value into the cache.
    *
    * Prefer `caching` instead of this function.
    */
  def put[V, Repr](key: CacheKey, value: V): Future[Unit]

  /**
    * Manually get a value from the cache.
    *
    * Prefer `caching` instead of this function.
    */
  def get[V](key: CacheKey): Future[Option[V]]

  /**
    * Flush (clear) the cache of all entries.
    */
  def flush(): Future[Unit]
}

object MemoryCache {
  private val flags = Flags(readsEnabled = true, writesEnabled = true)

  def newCache(name: String, maxSize: Long, ttl: Duration)(implicit ex: Executor, statsReceiver: StatsReceiver): MemoryCache =
    new MemoryCache {
      private val scalaTtl = ScalaDuration(ttl.inNanoseconds, TimeUnit.NANOSECONDS)
      private val cache: ScalaCache[InMemoryRepr] = CacheOps.newCache(name, maxSize, scalaTtl, ex)(statsReceiver)
      private val ec = fromExecutor(ex)

      statsReceiver.scope(sanitiseCacheName(name)).addGauge("size")(estimatedSize.getOrElse(0L).toFloat)

      sys.addShutdownHook(cache.cache.close())

      override def caching[V](key: CacheKey)(f: => Future[V]) = {
        val noOpCodec = Codec.anyToNoSerialization[V]
        scalacache.cachingWithTTL[V, InMemoryRepr](key)(scalaTtl)(f.asScala)(cache, flags, ec, noOpCodec).asTwitter(ec)
      }

      override def put[V, Repr](key: CacheKey, value: V): Future[Unit] = {
        val noOpCodec = Codec.anyToNoSerialization[V]
        scalacache.put[V, InMemoryRepr](key)(value, Some(scalaTtl))(cache, flags, noOpCodec).asTwitter(ec)
      }

      override def get[V](key: CacheKey): Future[Option[V]] = {
        val noOpCodec = Codec.anyToNoSerialization[V]
        scalacache.get[V, InMemoryRepr](key)(cache, flags, noOpCodec).asTwitter(ec)
      }

      override def flush() = cache.cache.removeAll().asTwitter(ec)

      private def estimatedSize = cache.cache match {
        case NonLoggingCaffeineCache(underlying) => Some(underlying.estimatedSize())
        case _ => None
      }
    }
}
