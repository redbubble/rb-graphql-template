package com.redbubble.hawk.validate

import com.redbubble.hawk._
import com.redbubble.hawk.params.{Nonce, PayloadHash}
import com.redbubble.hawk.parse.HawkArbitraries._
import com.redbubble.hawk.parse.RequestAuthorisationHeaderParser
import com.redbubble.hawk.spec.Generators
import com.redbubble.hawk.util.Time
import com.redbubble.util.spec.SpecHelper
import org.scalacheck.Prop._
import org.scalacheck.Properties
import org.specs2.mutable.Specification

final class RequestAuthorisationHeaderSpec extends Specification with SpecHelper with Generators {
  val headerProps = new Properties("Producing Hawk headers") {
    property("roundtrip") = forAll {
      (keyId: KeyId, timestamp: Time, nonce: Nonce, payloadHash: Option[PayloadHash], extendedData: Option[ExtendedData], mac: MAC) => {
        val lossyTime = Time.time(timestamp.asSeconds)
        val expected = RequestAuthorisationHeader(keyId, lossyTime, nonce, payloadHash, extendedData, mac)
        val parsed = RequestAuthorisationHeaderParser.parseAuthHeader(expected.httpHeaderForm)
        parsed must beSome(expected)
      }
    }
  }

  s2"Producing raw form headers$headerProps"
}
