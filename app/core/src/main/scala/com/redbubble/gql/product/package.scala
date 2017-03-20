package com.redbubble.gql

import shapeless.tag
import shapeless.tag._

trait CategoryNameTag

package object product {
  type CategoryName = String @@ CategoryNameTag

  def CategoryName(n: String): @@[String, CategoryNameTag] = tag[CategoryNameTag](n)
}
