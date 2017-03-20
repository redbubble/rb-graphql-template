package com.redbubble.hawk.validate

import java.nio.charset.StandardCharsets._

import com.redbubble.hawk._
import com.redbubble.hawk.params._
import com.redbubble.hawk.spec.Generators
import com.redbubble.hawk.util.Seconds
import com.redbubble.hawk.util.Time.time
import com.redbubble.hawk.validate.NormalisedRequest.{normalisedHeaderMac, normalisedPayloadMac}
import com.redbubble.util.spec.SpecHelper
import org.specs2.mutable.Specification

final class MaccerSpec extends Specification with SpecHelper with Generators {
  val keyId = KeyId("dh37fgj492je")
  val key = Key("werxhqb98rpaxn39848xrunpaw3489ruxnpa98w4rxn")
  val host = Host("example.com")
  val port = Port(8000)
  val path = UriPath("/resource/1?b=1&a=2")
  val timestamp = time(Seconds(1465190788))
  val nonce = Nonce(Base64Encoded("j4h3g2"))
  val extendedData = ExtendedData("some-app-ext-data")

  "Header validation" >> {
    val method = Get
    val authHeader = RequestAuthorisationHeader(
      keyId, timestamp, nonce, Some(PayloadHash(Base64Encoded("Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY="))),
      Some(extendedData), MAC(Base64Encoded("6R4rV5iE+NPoym+WwjeHzjAGXUtLNIxmo1vpMofpLAE=")))
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
      val mac = Maccer.validateRequestMac(credentials, noPayloadRequestContext, HeaderValidationMethod)
      mac must beRight(MacOps.mac(credentials, normalisedRequest.getBytes(UTF_8)))
    }

    "can be hashed as SHA-512" >> {
      val credentials = Credentials(keyId, key, Sha512)
      val mac = Maccer.validateRequestMac(credentials, noPayloadRequestContext, HeaderValidationMethod)
      mac must beRight(MacOps.mac(credentials, normalisedRequest.getBytes(UTF_8)))
    }

    "returns an error if we try to validate the payload" >> {
      val credentials = Credentials(keyId, key, Sha256)
      val mac = Maccer.validateRequestMac(credentials, noPayloadRequestContext, PayloadValidationMethod)
      mac must beLeft
    }
  }

  "Payload validation" >> {
    val payload = PayloadContext(ContentType("text/plain"), "Thank you for flying Hawk".getBytes(UTF_8))

    "can be hashed" >> {
      val authHeader = RequestAuthorisationHeader(
        keyId, timestamp, nonce, Some(PayloadHash(Base64Encoded("Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY="))),
        Some(extendedData), MAC(Base64Encoded("SYfiySDsJeAxYe8434iohquXpd5SgNWl0QDXAE1ZpW0=")))
      val requestContext = ValidatableRequestContext(RequestContext(Post, host, port, path, Some(payload)), authHeader)
      val credentials = Credentials(keyId, key, Sha256)
      val mac = Maccer.validateRequestMac(credentials, requestContext, PayloadValidationMethod)
      mac must beRight(normalisedRequestMac(credentials, requestContext, payload))
    }

    "failure is short circuited if an invalid hash is provided by the client" >> {
      val authHeader = RequestAuthorisationHeader(
        keyId, timestamp, nonce, Some(PayloadHash(Base64Encoded("INVALID HASH"))),
        Some(extendedData), MAC(Base64Encoded("6R4rV5iE+NPoym+WwjeHzjAGXUtLNIxmo1vpMofpLAE=")))
      val requestContext = ValidatableRequestContext(RequestContext(Post, host, port, path, Some(payload)), authHeader)
      val credentials = Credentials(keyId, key, Sha256)
      val mac = Maccer.validateRequestMac(credentials, requestContext, PayloadValidationMethod)
      mac must beLeft
    }
  }

  def normalisedRequestMac(credentials: Credentials, context: ValidatableRequestContext, payload: PayloadContext): MAC = {
    normalisedHeaderMac(credentials, context, Some(normalisedPayloadMac(credentials, payload)))
  }
}
