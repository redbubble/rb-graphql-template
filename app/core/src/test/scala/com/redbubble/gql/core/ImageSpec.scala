package com.redbubble.gql.core

import java.net.URL

import com.redbubble.gql.core.Image.imageFromUrl
import com.redbubble.gql.magickraum.MagickraumHelper
import com.redbubble.util.spec.SpecHelper
import org.scalacheck.Prop._
import org.scalacheck.Properties
import org.specs2.mutable.Specification

final class ImageSpec extends Specification with SpecHelper with MagickraumHelper {
  val imageParseProp = new Properties("Image parsing") {
    property("from known URLs with sizes") = forAll(genImageUrl) { (u: URL) =>
      val image = imageFromUrl(u)
      image.url must beEqualTo(u)
      image.size must beSome
    }

    property("from URLs with no sizing information") = forAll(genNoSizeImageUrl) { (u: URL) =>
      val image = imageFromUrl(u)
      image.url must beEqualTo(u)
      image.size must beNone
    }

    property("100x100") = forAll(gen100ImageUrl) { (u: URL) =>
      imageFromUrl(u).size must beSome(IrregularImageSize(PixelWidth(100), PixelHeight(100)))
    }

    property("192x192") = forAll(gen192ImageUrl) { (u: URL) =>
      imageFromUrl(u).size must beSome(Size_192x192)
    }

    property("200x200") = forAll(gen200ImageUrl) { (u: URL) =>
      imageFromUrl(u).size must beSome(IrregularImageSize(PixelWidth(200), PixelHeight(200)))
    }

    property("590x640") = forAll(gen590ImageUrl) { (u: URL) =>
      imageFromUrl(u).size must beSome(IrregularImageSize(PixelWidth(590), PixelHeight(640)))
    }
  }

  s2"Parsing an image from a URL$imageParseProp"
}
