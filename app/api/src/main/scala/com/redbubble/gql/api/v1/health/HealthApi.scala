package com.redbubble.gql.api.v1.health

import com.redbubble.util.http.ResponseOps._
import com.twitter.finagle.http.{Response, Status}
import com.twitter.io.Buf
import io.finch.{Endpoint, _}

object HealthApi {
  val healthApi = health

  def health: Endpoint[Response] = get("health") {
    textResponse(Status.Ok, Buf.Utf8("OK"))
  }
}
