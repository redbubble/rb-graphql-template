package com.redbubble.hawk.parse

import com.redbubble.hawk._
import com.redbubble.hawk.params.{Nonce, PayloadHash}
import com.redbubble.hawk.spec.Generators
import com.redbubble.hawk.util.Time
import com.redbubble.hawk.validate.{MAC, RequestAuthorisationHeader}
import com.redbubble.util.spec.SpecHelper
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Gen, Properties}
import org.specs2.mutable.Specification

//noinspection ScalaStyle
final class RequestAuthorisationHeaderParserSpec extends Specification with SpecHelper with Generators {
  val noHawkId = List(
    """Hawke id="dh37fgj492je", ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="""",
    """Hawkid="dh37fgj492je", ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="""",
    """Mac id="dh37fgj492je", ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="""",
    """Auth id="dh37fgj492je", ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw=""""
  )
  val missingFields = List(
    """Hawk ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="""",
    """Hawk id="dh37fgj492je, nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="""",
    """Hawk id="dh37fgj492je", ts="1353832234", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="""",
    """Hawk id="dh37fgj492je", ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data""""
  )
  val malformedFields = List(
    """Hawk id=dh37fgj492je", ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="""",
    """Hawk id="dh37fgj492je, ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="""",
    """Hawk id="dh37fgj492je" ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw=""""
  )

  val genKnownInvalidHeaders: Gen[RawAuthenticationHeader] =
    Gen.oneOf((noHawkId ++ missingFields ++ malformedFields).map(RawAuthenticationHeader))
  val genRandomStrings: Gen[RawAuthenticationHeader] = Arbitrary.arbString.arbitrary.map(RawAuthenticationHeader)

  val invalidHeadersProp = new Properties("Invalid/unsupported header parsing") {
    property("known invalid headers") = forAll(genKnownInvalidHeaders) { (header: RawAuthenticationHeader) =>
      val parsed = RequestAuthorisationHeaderParser.parseAuthHeader(header)
      parsed must beNone
    }
    property("random invalid headers") = forAll(genRandomStrings) { (header: RawAuthenticationHeader) =>
      val parsed = RequestAuthorisationHeaderParser.parseAuthHeader(header)
      parsed must beNone
    }
  }

  s2"Parsing invalid/unsupported authentication headers$invalidHeadersProp"

  import HawkArbitraries._

  val knownGoodHeaders = List(
    """Hawk id="dh37fgj492je", ts="1353832234", nonce="j4h3g2", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="""",
    """Hawk id="API Client", ts="1465180293", nonce="SBUyjx", mac="umWzFSjFkf+blSLH57gchUvm106bgxaaLLAVkU+fMy4=""""",
    """Hawk id="dh37fgj492je", ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw=""""
  )
  val genKnownValidHeaders: Gen[RawAuthenticationHeader] = Gen.oneOf(knownGoodHeaders).map(RawAuthenticationHeader)

  val parseProp = new Properties("Auth header parsing") {
    property("known valid headers") = forAll(genKnownValidHeaders) { (header: RawAuthenticationHeader) =>
      val parsed = RequestAuthorisationHeaderParser.parseAuthHeader(header)
      parsed must beSome
    }

    // Note. We re-parse the time here & below so that we loose millisecond precision, i.e. what we would get passed from a client call.
    property("generated headers") = forAll {
      (keyId: KeyId, timestamp: Time, nonce: Nonce, payloadHash: Option[PayloadHash], extendedData: Option[ExtendedData], mac: MAC) => {
        val lossyTime = Time.time(timestamp.asSeconds)
        val expected = RequestAuthorisationHeader(keyId, lossyTime, nonce, payloadHash, extendedData, mac)
        val parsed = RequestAuthorisationHeaderParser.parseAuthHeader(expected.httpHeaderForm)
        parsed must beSome(expected)
      }
    }
  }

  s2"Parsing authentication header$parseProp"
}
