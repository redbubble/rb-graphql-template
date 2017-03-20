package com.redbubble.gql.core

import com.redbubble.gql.graphql.schema._

sealed abstract class ImageSize(val width: PixelWidth, val height: PixelHeight) {
  def dimensions: String = s"${width}x${height}"
}

final case class IrregularImageSize(override val width: PixelWidth, override val height: PixelHeight)
    extends ImageSize(width, height)

//
// Note. Recommended images sizes from: https://redbubble.atlassian.net/wiki/display/ENG/Artwork+and+product+image+size+availability
//

abstract class RegularImageSize(override val width: PixelWidth, override val height: PixelHeight)
    extends ImageSize(width, height)

case object Size_64x64 extends RegularImageSize(PixelWidth(64), PixelHeight(64))

case object Size_96x96 extends RegularImageSize(PixelWidth(96), PixelHeight(96))

case object Size_128x128 extends RegularImageSize(PixelWidth(128), PixelHeight(128))

case object Size_192x192 extends RegularImageSize(PixelWidth(192), PixelHeight(192))

case object Size_256x256 extends RegularImageSize(PixelWidth(256), PixelHeight(256))

case object Size_384x384 extends RegularImageSize(PixelWidth(384), PixelHeight(384))

case object Size_480x480 extends RegularImageSize(PixelWidth(480), PixelHeight(480))

case object Size_768x768 extends RegularImageSize(PixelWidth(768), PixelHeight(768))

case object Size_960x960 extends RegularImageSize(PixelWidth(960), PixelHeight(960))

case object Size_1024x1024 extends RegularImageSize(PixelWidth(1024), PixelHeight(1024))

object ImageSize {
  val validImageSizes: Seq[ImageSize] = List(
    Size_64x64, Size_96x96, Size_128x128, Size_192x192, Size_256x256, Size_384x384, Size_480x480, Size_768x768,
    Size_960x960, Size_1024x1024
  )

  def regularImageSize(width: PixelWidth, height: PixelHeight): Option[ImageSize] =
    validImageSizes.find(s => s.width == width && s.height == height)

  def sizeForSizeClass(sizeClass: SizeClass): ImageSize =
    regularImageSize(PixelWidth(sizeClass), PixelHeight(sizeClass)).getOrElse(DefaultImageSize)
}
