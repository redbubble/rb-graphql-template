package com.redbubble.gql.core

final case class Coordinate(x: PixelWidth, y: PixelHeight)

final case class ImageCropBox(origin: Coordinate, width: PixelWidth, height: PixelHeight)
