package com.redbubble.hawk.parse

import com.redbubble.hawk._
import com.redbubble.hawk.params.{Nonce, PayloadHash}
import com.redbubble.hawk.parse.HeaderKeyValueParser.parseKeyValue
import com.redbubble.hawk.util.Time.parseSecondsAsTimeUtc
import com.redbubble.hawk.validate.{MAC, RequestAuthorisationHeader}

object RequestAuthorisationHeaderParser {
  def parseAuthHeader(header: RawAuthenticationHeader): Option[RequestAuthorisationHeader] =
    if (header.startsWith(s"$HawkHeaderValuePrefix ")) {
      parseSupportedHeader(header)
    } else {
      None
    }

  private def parseSupportedHeader(header: RawAuthenticationHeader): Option[RequestAuthorisationHeader] = {
    val kvs = parseKeyValues(header)
    for {
      id <- kvs.get(HeaderKey("id")).map(KeyId)
      timestamp <- kvs.get(HeaderKey("ts")).flatMap(parseSecondsAsTimeUtc)
      nonce <- kvs.get(HeaderKey("nonce")).map(n => Nonce(Base64Encoded(n)))
      mac <- kvs.get(HeaderKey("mac")).map(m => MAC(Base64Encoded(m)))
    } yield {
      val payloadHash = kvs.get(HeaderKey("hash")).map(h => PayloadHash(Base64Encoded(h)))
      val extendedData = kvs.get(HeaderKey("ext")).map(ExtendedData)
      RequestAuthorisationHeader(id, timestamp, nonce, payloadHash, extendedData, mac)
    }
  }

  private def parseKeyValues(header: RawAuthenticationHeader): Map[HeaderKey, HeaderValue] = {
    val kvs = header.split(",").map(kv => parseKeyValue(HeaderKeyValue(kv)))
    kvs.foldLeft(Map[HeaderKey, HeaderValue]())((acc, maybeKv) => acc ++ maybeKv.getOrElse(Map()))
  }
}
