package com.redbubble.gql.backends.works

import com.redbubble.gql.product.Work
import com.redbubble.gql.util.spec.ModelGenerators
import com.redbubble.util.spec.SpecHelper
import org.scalacheck.Prop._
import org.scalacheck.Properties
import org.specs2.mutable.Specification

final class WorkDecodersSpec extends Specification with SpecHelper with ModelGenerators with WorkApiJson {
  val decodingProp = new Properties("Related works decoding") {
    property("decoding") = forAll { (work: Work) =>
      val json = feedJson(Seq(work))
      val decoded = decode(json)(WorksDecoders.workDataDecoder)
      decoded must beRight(Seq(work))
    }
  }

  s2"Decoding a work feed$decodingProp"
}
