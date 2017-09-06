package com.redbubble.gql.api.v1.explore

import com.redbubble.util.http.ResponseOps._
import com.redbubble.util.io.Resource._
import com.twitter.concurrent.AsyncStream
import com.twitter.conversions.storage._
import com.twitter.finagle.http.{Response, Status}
import com.twitter.io.Buf
import com.twitter.io.Reader.fromStream
import io.finch.{Endpoint, _}

object ExploreApi {
  private val graphiQlPath = "/graphiql.html"
  val exploreApi = explore

  def explore: Endpoint[Response] = get("explore") {
    classpathResource(graphiQlPath).map(fromStream) match {
      case Some(content) => asyncHtmlResponse(Status.Ok, AsyncStream.fromReader(content, chunkSize = 512.kilobytes.inBytes.toInt))
      case None => textResponse(Status.InternalServerError, Buf.Utf8(s"Unable to find GraphiQL at '$graphiQlPath'"))
    }
  }
}
