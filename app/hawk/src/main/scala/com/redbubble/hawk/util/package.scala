package com.redbubble.hawk

import shapeless.tag
import shapeless.tag.@@

trait MillisTag

trait SecondsTag

package object util {

  type Millis = Long @@ MillisTag
  type Seconds = Long @@ SecondsTag

  def Millis(m: Long): @@[Long, MillisTag] = tag[MillisTag](m)

  def Seconds(s: Long): @@[Long, SecondsTag] = tag[SecondsTag](s)
}
