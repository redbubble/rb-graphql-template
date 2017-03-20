package com.redbubble.gql.api.v1.admin

import com.redbubble.gql.cache.Cache._
import com.redbubble.gql.util.log.CoreLogger
import com.redbubble.util.http.ResponseOps._
import com.twitter.finagle.http.{Response, Status}
import com.twitter.io.Buf
import com.twitter.util.Future
import io.finch.{Endpoint, _}

object AdminApi extends CoreLogger {
  val adminApi = flush_cache

  def flush_cache: Endpoint[Response] = post("admin" :: "flush_cache") {
    log.info(s"Cache flush requested")
    val flushResult = Future.join(Seq(graphQlQueryCache.flush(), generalCache.flush()))
    flushResult.map(_ => textResponse(Status.Ok, Buf.Utf8("OK")))
  }
}
