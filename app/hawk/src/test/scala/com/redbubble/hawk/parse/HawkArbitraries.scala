package com.redbubble.hawk.parse

import com.redbubble._
import com.redbubble.hawk._
import com.redbubble.hawk.params.{Nonce, PayloadHash}
import com.redbubble.hawk.validate.MAC
import com.redbubble.util.spec.gen.Generators
import org.scalacheck.{Arbitrary, Gen}
import shapeless.tag.@@

object HawkArbitraries {
  private val genNonEmptyString: Gen[String] = Gen.alphaStr.suchThat(i => !i.isEmpty)
  private val genNonEmptyNumString: Gen[String] = Gen.numStr.suchThat(i => !i.isEmpty)

  implicit val arbKeyId = Arbitrary(Generators.genHexOfLength(12)(KeyId))
  implicit val arbNonce = Arbitrary(genNonEmptyNumString.map(n => Nonce(Base64Encoded(n))))
  implicit val arbPayloadHash: Arbitrary[PayloadHash] = Arbitrary(Generators.genHexOfLength(44)(s => PayloadHash(Base64Encoded(s))))
  implicit val arbOptionPayloadHash: Arbitrary[Option[PayloadHash]] = Arbitrary(arbPayloadHash.arbitrary.map(p => Some(p)))
  implicit val arbExtendedData: Arbitrary[@@[String, ExtendedDataTag]] = Arbitrary(genNonEmptyString.map(ExtendedData))
  implicit val arbOptionExtendedData: Arbitrary[Option[@@[String, ExtendedDataTag]]] = Arbitrary(arbExtendedData.arbitrary.map(e => Some(e)))
  implicit val arbMac = Arbitrary(Generators.genHexOfLength(44)(h => MAC(Base64Encoded(h))))
}
