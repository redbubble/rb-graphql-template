package com.redbubble.util.io

import java.io.InputStream

import com.twitter.io.Reader._
import com.twitter.io.{Buf, Reader}
import com.twitter.util.{Await, Future}

trait Resource {
  final def classpathResource(name: String): Option[InputStream] = Option(getClass.getResourceAsStream(name))

  final def classpathResourceAsBuf(path: String): Option[Buf] =
    asyncClasspathResourceAsBuf(path).map(f => Await.result(f))

  final def asyncClasspathResourceAsBuf(path: String): Option[Future[Buf]] =
    classpathResource(path).map(s => Reader.readAll(fromStream(s)))
}

object Resource extends Resource
