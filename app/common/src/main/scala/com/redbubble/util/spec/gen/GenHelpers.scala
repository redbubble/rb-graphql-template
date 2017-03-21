package com.redbubble.util.spec.gen

import org.scalacheck.Gen

trait GenHelpers {
  // Note. This sometimes fails, so we fetch it twice to reduce that liklihood, yes it's a hack
  final def sample[T](gen: Gen[T]): T = gen.sample.orElse(gen.sample).get

  final val genNonEmptyString: Gen[String] = Gen.alphaStr.suchThat(i => !i.isEmpty)

  final val genPositiveInt: Gen[Int] = Gen.posNum[Int]
}
