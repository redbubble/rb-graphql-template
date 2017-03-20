package com.redbubble.gql

import shapeless.tag
import shapeless.tag._

trait IdTag

trait PixelWidthTag

trait PixelHeightTag

trait SizeClassTag

package object core {
  type Id = String @@ IdTag

  type PixelWidth = Int @@ PixelWidthTag
  type PixelHeight = Int @@ PixelHeightTag
  type SizeClass = Int @@ SizeClassTag

  def Id(i: String): @@[String, IdTag] = tag[IdTag](i)

  def PixelWidth(w: Int): @@[Int, PixelWidthTag] = tag[PixelWidthTag](w)

  def PixelHeight(h: Int): @@[Int, PixelHeightTag] = tag[PixelHeightTag](h)

  def SizeClass(s: Int): @@[Int, SizeClassTag] = tag[SizeClassTag](s)
}
