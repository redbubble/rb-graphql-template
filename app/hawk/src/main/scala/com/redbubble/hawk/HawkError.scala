package com.redbubble.hawk

final class HawkError(reason: String, cause: Option[Throwable] = None) extends Throwable(reason) {
  override def getMessage: String = reason

  override def getCause: Throwable = cause.orNull
}
