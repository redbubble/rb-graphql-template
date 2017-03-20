package com.redbubble.util

import shapeless.tag
import shapeless.tag._

trait CacheKeyTag

package object cache {
  val EmptyValue = "empty"

  type CacheKey = String @@ CacheKeyTag

  def CacheKey(k: String): @@[String, CacheKeyTag] = tag[CacheKeyTag](k)
}
