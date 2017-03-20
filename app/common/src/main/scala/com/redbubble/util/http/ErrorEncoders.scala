package com.redbubble.util.http

import com.redbubble.util.json.ExceptionEncoder._
import io.finch.Encode

trait ErrorEncoders {
  implicit val exceptionEncode: Encode.Json[Exception] = ResponseOps.exceptionJsonEncode(exceptionEncoder)
  implicit val throwableEncode: Encode.Json[Throwable] = ResponseOps.throwableJsonEncode(throwableEncoder)
}

object ErrorEncoders extends ErrorEncoders
