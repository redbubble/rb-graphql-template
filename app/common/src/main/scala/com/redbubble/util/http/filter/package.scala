package com.redbubble.util.http

import com.twitter.finagle.SimpleFilter
import com.twitter.finagle.http.{Request, Response}

package object filter {
  type HttpFilter = SimpleFilter[Request, Response]
}
