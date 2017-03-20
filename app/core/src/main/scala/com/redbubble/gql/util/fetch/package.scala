package com.redbubble.gql.util

import com.redbubble.gql.cache.Cache.fetchedObjectCache
import com.redbubble.gql.util.async.futurePool
import com.redbubble.util.fetch.FetcherRunner

package object fetch {
  lazy val runner: FetcherRunner = FetcherRunner(fetchedObjectCache)(futurePool)
}
