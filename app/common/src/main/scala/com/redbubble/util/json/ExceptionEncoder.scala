package com.redbubble.util.json

import io.circe.syntax._
import io.circe.{Encoder, Json}

trait ExceptionEncoder {
  val exceptionEncoder: Encoder[Exception] = Encoder.instance[Exception](t => exceptionJson(t))
  val throwableEncoder: Encoder[Throwable] = Encoder.instance[Throwable](t => exceptionJson(t))

  final def exceptionJson(t: Throwable): Json = exceptionFields(t).asJson

  private def exceptionFields(t: Throwable): Map[String, String] = {
    val base = Map(
      "message" -> t.getMessage,
      "type" -> t.getClass.getSimpleName
    )
    base.++(Option(t.getCause).map(cause => Map("cause" -> cause.getMessage)).getOrElse(Map()))
  }
}

object ExceptionEncoder extends ExceptionEncoder
