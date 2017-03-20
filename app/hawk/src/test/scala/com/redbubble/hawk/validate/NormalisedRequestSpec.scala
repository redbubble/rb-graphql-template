package com.redbubble.hawk.validate

import java.nio.charset.StandardCharsets._

import com.redbubble.hawk._
import com.redbubble.hawk.params._
import com.redbubble.hawk.validate.NormalisedRequest._
import com.redbubble.util.spec.SpecHelper
import com.redbubble.hawk.util.Seconds
import com.redbubble.hawk.util.Time.time
import org.specs2.mutable.Specification

final class NormalisedRequestSpec extends Specification with SpecHelper {
  val keyId = KeyId("dh37fgj492je")
  val key = Key("werxhqb98rpaxn39848xrunpaw3489ruxnpa98w4rxn")
  val host = Host("example.com")
  val port = Port(8000)
  val path = UriPath("/resource/1?b=1&a=2")
  val timestamp = time(Seconds(1465190788))

  val nonce = Nonce(Base64Encoded("j4h3g2"))
  val extendedData = ExtendedData("some-app-ext-data")
  val payloadHash = PayloadHash(Base64Encoded("Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY="))
  val authHeader = RequestAuthorisationHeader(keyId, timestamp, nonce, Some(payloadHash),
    Some(extendedData), MAC(Base64Encoded("6R4rV5iE+NPoym+WwjeHzjAGXUtLNIxmo1vpMofpLAE=")))

  "Normalised headers without payload" >> {
    val method = Get
    val noPayloadRequestContext = ValidatableRequestContext(RequestContext(method, host, port, path, None), authHeader)
    val normalisedRequest =
      s"""
         |${HeaderValidationMethod.identifier}
         |${timestamp.asSeconds}
         |${nonce.encoded}
         |${method.httpRequestLineMethod}
         |${path.path}
         |${host.host}
         |${port.port}
         |
         |$extendedData
      """.stripMargin.trim + "\n"

    "can be hashed as SHA-256" >> {
      val credentials = Credentials(keyId, key, Sha256)
      val mac = normalisedHeaderMac(credentials, noPayloadRequestContext, None)
      mac must beEqualTo(MacOps.mac(credentials, normalisedRequest.getBytes(UTF_8)))
    }

    "can be hashed as SHA-512" >> {
      val credentials = Credentials(keyId, key, Sha512)
      val mac = normalisedHeaderMac(credentials, noPayloadRequestContext, None)
      mac must beEqualTo(MacOps.mac(credentials, normalisedRequest.getBytes(UTF_8)))
    }
  }

  "Normalised headers with payload" >> {
    val method = Get
    val payloadContext = PayloadContext(ContentType("text/plain"), "Thank you for flying Hawk".getBytes(UTF_8))
    val withPayloadRequestContext = ValidatableRequestContext(RequestContext(method, host, port, path, Some(payloadContext)), authHeader)
    val normalisedRequest =
      s"""
         |${HeaderValidationMethod.identifier}
         |${timestamp.asSeconds}
         |${nonce.encoded}
         |${method.httpRequestLineMethod}
         |${path.path}
         |${host.host}
         |${port.port}
         |${payloadHash.encoded}
         |$extendedData
      """.stripMargin.trim + "\n"

    "can be hashed as SHA-256" >> {
      val credentials = Credentials(keyId, key, Sha256)
      val mac = normalisedHeaderMac(credentials, withPayloadRequestContext, Some(MAC(payloadHash.encoded)))
      mac must beEqualTo(MacOps.mac(credentials, normalisedRequest.getBytes(UTF_8)))
    }

    "can be hashed as SHA-512" >> {
      val credentials = Credentials(keyId, key, Sha512)
      val mac = normalisedHeaderMac(credentials, withPayloadRequestContext, Some(MAC(payloadHash.encoded)))
      mac must beEqualTo(MacOps.mac(credentials, normalisedRequest.getBytes(UTF_8)))
    }
  }

  "Normalised payloads" >> {
    val payload = PayloadContext(ContentType("text/plain"), "Thank you for flying Hawk".getBytes(UTF_8))

    "can be hashed as SHA-256" >> {
      val credentials = Credentials(keyId, key, Sha256)
      val mac = normalisedPayloadMac(credentials, payload)
      mac must beEqualTo(MacOps.mac(credentials, normalisedPayloadRequest(credentials, payload).getBytes(UTF_8)))
    }

    "can be hashed as SHA-512" >> {
      val credentials = Credentials(keyId, key, Sha512)
      val mac = normalisedPayloadMac(credentials, payload)
      mac must beEqualTo(MacOps.mac(credentials, normalisedPayloadRequest(credentials, payload).getBytes(UTF_8)))
    }
  }

  def normalisedPayloadRequest(credentials: Credentials, payload: PayloadContext): String = {
    s"""
       |${PayloadValidationMethod.identifier}
       |${payload.contentType.contentType.toLowerCase}
       |${new String(payload.data, UTF_8)}
    """.stripMargin.trim + "\n"
  }
}
