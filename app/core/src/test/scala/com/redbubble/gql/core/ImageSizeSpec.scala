package com.redbubble.gql.core

import com.redbubble.gql.core.ImageSize.{regularImageSize, validImageSizes}
import com.redbubble.util.spec.SpecHelper
import org.scalacheck.Prop._
import org.scalacheck.{Gen, Properties}
import org.specs2.mutable.Specification

final class ImageSizeSpec extends Specification with SpecHelper {
  val genImageSize = Gen.oneOf(validImageSizes)

  val imageSizeProp = new Properties("Image dimensions") {
    property("dimensions") = forAll(genImageSize) { (s: ImageSize) =>
      s.dimensions must beEqualTo(s"${s.width}x${s.height}")
    }

    property("valid size parsing") = forAll(genImageSize) { (s: ImageSize) =>
      regularImageSize(s.width, s.height) must beSome
    }

    property("invalid size parsing") = forAll { (w: Int, h: Int) =>
      regularImageSize(PixelWidth(w), PixelHeight(h)) must beNone
    }

    property("sizes are all square") = forAll(genImageSize) { (s: ImageSize) =>
      s.width must beEqualTo(s.height)
    }
  }

  s2"Image dimensions$imageSizeProp"
}
