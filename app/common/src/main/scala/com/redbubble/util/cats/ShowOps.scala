package com.redbubble.util.cats

import cats.Show
import com.redbubble.util.io.BufOps._
import com.twitter.io.Buf

trait ShowOps {
  final val showBuf: Show[Buf] = new Show[Buf] {
    override def show(b: Buf): String = bufToString(b)
  }

  final val showThrowable: Show[Throwable] = new Show[Throwable] {
    override def show(t: Throwable): String = t.getMessage
  }
}

object ShowOps extends ShowOps
