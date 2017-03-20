package com.redbubble.util.http

final case class RelativePath(path: String)

object RelativePath {
  val emptyPath: RelativePath = RelativePath("")
}
