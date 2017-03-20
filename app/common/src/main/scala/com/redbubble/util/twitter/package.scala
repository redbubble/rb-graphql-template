package com.redbubble.util

import com.redbubble.util.std.EitherOps
import com.twitter.util.{Try => TwitterTry}

package object twitter {

  implicit final class RichTry[+A](val t: TwitterTry[A]) extends AnyVal {
    def toEither: Either[Throwable, A] = EitherOps.fromTry(t)
  }

}
