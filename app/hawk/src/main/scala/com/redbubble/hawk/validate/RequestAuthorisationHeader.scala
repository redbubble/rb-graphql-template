package com.redbubble.hawk.validate

import com.redbubble.hawk._
import com.redbubble.hawk.params.{Nonce, PayloadHash}
import com.redbubble.hawk.util.Time

final case class RequestAuthorisationHeader(keyId: KeyId,
    timestamp: Time, nonce: Nonce, payloadHash: Option[PayloadHash], extendedData: Option[ExtendedData], mac: MAC) {

  def httpHeaderForm: RawAuthenticationHeader = {
    val payloadKeys = payloadHash.map(hash => Map("hash" -> hash.encoded)).getOrElse(Map())
    val extKeys = extendedData.map(ext => Map("ext" -> ext)).getOrElse(Map())
    val kvs = Map(
      "id" -> keyId,
      "ts" -> timestamp.asSeconds.toString,
      "nonce" -> nonce.encoded,
      "mac" -> mac.encoded) ++ payloadKeys ++ extKeys
    val headerElements = kvs.map(kv => s"""${kv._1}="${kv._2}"""").mkString(", ")
    RawAuthenticationHeader(s"$HawkHeaderValuePrefix $headerElements")
  }
}
