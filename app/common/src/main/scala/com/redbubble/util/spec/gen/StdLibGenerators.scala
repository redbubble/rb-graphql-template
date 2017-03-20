package com.redbubble.util.spec.gen

import java.net.{URI, URL}

import org.scalacheck.Gen._
import org.scalacheck.{Arbitrary, Gen}

trait StdLibGenerators {
  private val validUris = List(
    "",
    "scheme:foo",
    "http://redbubble.com/",
    "http://ih0.redbubble.net/avatar.885694.140x140.jpg",
    "/assets/rb-default-avatar.140x140-ba04e19ea9d9caa82d160b8b8b52dfce.png",
    "urn:isbn:0-486-27557-4",
    "ftp://example.org/resource.txt",
    "#frag01"
  )
  private val validUrls = List(
    "http://redbubble.com/",
    "http://ih0.redbubble.net/avatar.885694.140x140.jpg",
    "http://ih1.redbubble.net/image.4670208.4590/ra,unisex_tshirt,x2000,fafafa:ca443f4786,front-c,400,410,590,640-pad,590x640,f8f8f8.jpg",
    "ftp://example.org/resource.txt",
    "ftp://////abcdfsd&^^^"
  )

  val genUri: Gen[URI] = Gen.oneOf(validUris).map(new URI(_))
  val genUrl: Gen[URL] = Gen.oneOf(validUrls).map(new URL(_))

  implicit val arbUrl: Arbitrary[URL] = Arbitrary(genUrl)

  val genExceptionNoCause: Gen[Exception] = alphaStr.map { m => new Exception(m) }
  val genExceptionCause: Gen[Exception] = for {
    m <- alphaStr
    c <- genExceptionNoCause
  } yield new Exception(m, c)

  val genException: Gen[Exception] = oneOf(genExceptionNoCause, genExceptionCause)

  implicit def arbException: Arbitrary[Exception] = Arbitrary(genException)

  val genThrowableNoCause: Gen[Throwable] = alphaStr.map { m => new Throwable(m) }
  val genThrowableCause: Gen[Throwable] = for {
    m <- alphaStr
    c <- genThrowableNoCause
  } yield new Throwable(m, c)

  val genThrowable: Gen[Throwable] = oneOf(genThrowableNoCause, genThrowableCause)

  implicit def arbThrowable: Arbitrary[Throwable] = Arbitrary(genThrowable)
}
