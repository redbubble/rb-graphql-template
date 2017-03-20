package com.redbubble.hawk.validate

import java.nio.charset.StandardCharsets._
import javax.crypto
import javax.crypto.spec.SecretKeySpec

import com.redbubble.hawk.validate.Base64Ops._

trait MacOps {
  final def mac(credentials: Credentials, data: Array[Byte]): MAC = MAC(base64Encode(createMac(credentials, data)))

  private def createMac(credentials: Credentials, data: Array[Byte]): Array[Byte] = {
    val mac = crypto.Mac.getInstance(credentials.algorithm.keyGeneratorAlgorithm)
    val key = new SecretKeySpec(credentials.key.getBytes(UTF_8), credentials.algorithm.keyGeneratorAlgorithm)
    mac.init(key)
    mac.doFinal(data)
  }
}

object MacOps extends MacOps
