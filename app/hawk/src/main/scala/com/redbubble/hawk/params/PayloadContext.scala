package com.redbubble.hawk.params

import java.security.SecureRandom

import com.redbubble.hawk.Base64Encoded
import com.redbubble.hawk.validate.Base64Ops.base64Encode

object ContentType {
  val UnknownContentType: ContentType = ContentType("application/octet-stream")
}

final case class Nonce(encoded: Base64Encoded)

object Nonce {
  private lazy val numberGenerator = new SecureRandom

  def generateNonce: Nonce = {
    val bytes = new Array[Byte](6)
    numberGenerator.nextBytes(bytes)
    Nonce(base64Encode(bytes))
  }
}

final case class PayloadHash(encoded: Base64Encoded)

final case class ContentType(contentType: String)

final case class PayloadContext(contentType: ContentType, data: Array[Byte])
