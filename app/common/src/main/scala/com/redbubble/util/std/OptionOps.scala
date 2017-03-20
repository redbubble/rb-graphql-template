package com.redbubble.util.std

import com.twitter.util.{Return, Throw, Try => TwitterTry}

import scala.util.{Failure, Success, Try => ScalaTry}

trait OptionOps {
  def asScalaTry[A](o: Option[A], onNone: Throwable): ScalaTry[A] = o.fold[ScalaTry[A]](Failure(onNone))(a => Success(a))

  def asTwitterTry[A](o: Option[A], onNone: Throwable): TwitterTry[A] = o.fold[TwitterTry[A]](Throw(onNone))(a => Return(a))
}

object OptionOps extends OptionOps
