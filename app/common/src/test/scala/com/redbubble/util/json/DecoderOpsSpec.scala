package com.redbubble.util.json

import java.net.{URI, URL}

import com.redbubble.util.io.BufOps
import com.redbubble.util.spec.SpecHelper
import com.twitter.io.Buf
import org.scalacheck.Prop._
import org.scalacheck.{Gen, Properties}
import org.specs2.mutable.Specification

final class DecoderOpsSpec extends Specification with SpecHelper {
  val invalidUris = List("ftp://////abcdfsd&^^^", "this is not valid")
  val invalidUrls = List(
    "",
    "scheme:foo",
    "/assets/rb-default-avatar.140x140-ba04e19ea9d9caa82d160b8b8b52dfce.png",
    "urn:isbn:0-486-27557-4",
    "this is not valid",
    "#frag01"
  )

  val genInvalidUris: Gen[String] = Gen.oneOf(invalidUris)
  val genInvalidUrls: Gen[String] = Gen.oneOf(invalidUrls)

  val uriParsingProp = new Properties("URI decoding") {
    property("valid URIs") = forAll(genUri) { (u: URI) =>
      val decoded = decode(buildJson(u.toString))(DecoderOps.uriDecoder)
      decoded must beRight
    }

    property("invalid URIs") = forAll(genInvalidUris) { (s: String) =>
      val decoded = decode(buildJson(s))(DecoderOps.uriDecoder)
      decoded must beLeft
    }
  }

  s2"Parsing URIs$uriParsingProp"

  val urlParsingProp = new Properties("URL decoding") {
    property("valid URLs") = forAll(genUrl) { (u: URL) =>
      val decoded = decode(buildJson(u.toString))(DecoderOps.urlDecoder)
      decoded must beRight
    }

    property("invalid URLs") = forAll(genInvalidUrls) { (s: String) =>
      val decoded = decode(buildJson(s))(DecoderOps.urlDecoder)
      decoded must beLeft
    }
  }

  s2"Parsing URLs$urlParsingProp"

  private def buildJson(s: String): Buf = BufOps.stringToBuf(s""""$s"""")
}
