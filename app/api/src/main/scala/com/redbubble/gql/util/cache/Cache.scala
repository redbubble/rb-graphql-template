package com.redbubble.gql.util.cache

import com.redbubble.gql.util.async.executorService
import com.redbubble.gql.util.config.Config
import com.redbubble.gql.util.metrics.Metrics.serverMetrics
import com.redbubble.util.cache.MemoryCache
import com.redbubble.util.cache.MemoryCache.newCache

object Cache {
  /**
    * A long lived (`Config.generalCacheTtl`), general purpose, in-memory cache.
    */
  lazy val generalCache: MemoryCache =
    newCache("cache_general", Config.generalCacheSize, Config.generalCacheTtl)(executorService, serverMetrics)

  /**
    * An in-memory cache for GraphQL queries.
    */
  lazy val graphQlQueryCache: MemoryCache =
    newCache("cache_graphql_query", Config.graphQlQueryCacheSize, Config.graphQlQueryCacheTtl)(executorService, serverMetrics)

  /**
    * A cache for storing the results of fetches from downstream services.
    */
  lazy val fetchedObjectCache: MemoryCache =
    newCache("cache_fetched_object", Config.fetchedObjectCacheSize, Config.fetchedObjectCacheTtl)(executorService, serverMetrics)
}
