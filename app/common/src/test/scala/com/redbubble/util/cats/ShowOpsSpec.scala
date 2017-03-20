package com.redbubble.util.cats

import java.nio.charset.StandardCharsets._

import com.redbubble.util.io.BufOps
import com.redbubble.util.spec.SpecHelper
import com.twitter.io.Buf
import org.scalacheck.Prop._
import org.scalacheck.Properties
import org.specs2.mutable.Specification

final class ShowOpsSpec extends Specification with SpecHelper {

  val showBuffProp = new Properties("Show[Buf]") {
    property("roundtrip") = forAll(genStringBuf) { (b: Buf) =>
      BufOps.stringToBuf(ShowOps.showBuf.show(b), UTF_8) must beEqualTo(b)
    }
  }

  s2"Show for Buf$showBuffProp"

  val showThrowableProp = new Properties("Show[Throwable]") {
    property("roundtrip") = forAll(genThrowable) { (t: Throwable) =>
      ShowOps.showThrowable.show(t) must beEqualTo(t.getMessage)
    }
  }

  s2"Show for Throwable$showThrowableProp"
}
