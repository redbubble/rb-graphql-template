package com.redbubble.util.std

import com.twitter.util.{Try => TwitterTry}

import scala.util.{Try => ScalaTry}

package object syntax {

  implicit final class RichOption[+A](val option: Option[A]) extends AnyVal {
    def toScalaTry(onNone: Throwable): ScalaTry[A] = OptionOps.asScalaTry(option, onNone)

    def toTwitterTry(onNone: Throwable): TwitterTry[A] = OptionOps.asTwitterTry(option, onNone)
  }

}
