package com.redbubble.gql.core

import java.net.URL

import com.redbubble.gql.core.ImageSize.regularImageSize
import mouse.string._

import scala.util.matching.Regex

final case class Image(url: URL, size: Option[ImageSize], cropBox: Option[ImageCropBox] = None)

object Image {
  private val dimensionsRegex = new Regex("([0-9]+)x([0-9]+)", "w", "h")

  def imageFromUrl(u: URL): Image = Image(u, imageSize(u), None)

  private def imageSize(u: URL): Option[ImageSize] = {
    val matches = dimensionsRegex.findAllMatchIn(u.toString)
    matches.toList.lastOption.flatMap { m =>
      for {
        width <- m.group("w").parseIntOption.map(PixelWidth)
        height <- m.group("h").parseIntOption.map(PixelHeight)
      } yield regularImageSize(width, height).getOrElse(IrregularImageSize(width, height))
    }
  }
}
