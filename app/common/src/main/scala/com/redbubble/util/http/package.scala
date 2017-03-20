package com.redbubble.util

import shapeless.tag
import shapeless.tag.@@
import com.twitter.util.Future

trait QueryParamTag

trait HttpHeaderTag

package object http {
  type DownstreamResponse[A] = Future[Either[ApiError, A]]

  type QueryParamTuple = (String, String)
  type HttpHeaderTuple = (String, String)

  type QueryParam = QueryParamTuple @@ QueryParamTag
  type HttpHeader = HttpHeaderTuple @@ HttpHeaderTag

  def QueryParam(p: QueryParamTuple): QueryParamTuple @@ QueryParamTag = tag[QueryParamTag](p)

  def HttpHeader(h: HttpHeaderTuple): HttpHeaderTuple @@ HttpHeaderTag = tag[HttpHeaderTag](h)
}
