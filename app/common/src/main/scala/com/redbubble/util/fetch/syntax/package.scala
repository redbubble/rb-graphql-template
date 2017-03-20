package com.redbubble.util.fetch

import com.twitter.util.Future
import fetch.Fetch

package object syntax {

  implicit final class RunnableFetch[T](fetch: Fetch[T]) {
    /**
      * Runs this fetch, returning a `Future` containing the result.
      */
    def runF(implicit runner: FetcherRunner): Future[T] = runner.runFetchF(fetch)
  }

}
