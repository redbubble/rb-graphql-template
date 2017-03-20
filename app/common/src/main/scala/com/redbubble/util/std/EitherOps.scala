package com.redbubble.util.std

import com.twitter.util.{Return, Throw, Try => TwitterTry}

trait EitherOps {
  final def fromTry[A](t: TwitterTry[A]): Either[Throwable, A] = t match {
    case Throw(e) => Left(e)
    case Return(v) => Right(v)
  }
}

object EitherOps extends EitherOps
