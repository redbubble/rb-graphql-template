package com.redbubble.gql.fetch

import com.redbubble.util.async.FutureOps.flattenFuture
import com.redbubble.util.http.DownstreamResponse
import fetch.Query

object DataSourceOps {
  /**
    * Builds a `Query` suitable for `DataSource.fetchOne` from an async function.
    */
  def asyncQuery[A](f: => DownstreamResponse[A]): Query[Option[A]] =
    Query.async { (ok, error) =>
      flattenFuture(f).onSuccess(ws => ok(Some(ws))).onFailure(error)
      ()
    }
}
