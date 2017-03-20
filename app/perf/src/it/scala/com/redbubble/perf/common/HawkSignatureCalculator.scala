package com.redbubble.perf.common

import java.nio.charset.StandardCharsets._

import com.redbubble.hawk.AuthorisationHttpHeader
import com.redbubble.hawk.HawkAuthenticate.hawkHeader
import com.redbubble.hawk.params.HttpMethod.httpMethod
import com.redbubble.hawk.params._
import com.redbubble.hawk.validate.Credentials
import io.gatling.http.Predef._
import org.asynchttpclient.{RequestBuilderBase, SignatureCalculator}

private final class HawkSignatureCalculator(credentials: Credentials, contentType: ContentType)
    extends SignatureCalculator {
  override def calculateAndAddSignature(request: Request, requestBuilder: RequestBuilderBase[_]) = {
    val method = httpMethod(request.getMethod).getOrElse(Get)
    val uri = request.getUri
    val payload = PayloadContext(contentType, request.getStringData.getBytes(UTF_8))
    val context = RequestContext(method, Host(uri.getHost), Port(uri.getPort), UriPath(uri.getPath), Some(payload))
    val header = hawkHeader(credentials, context).httpHeaderForm
    requestBuilder.addHeader(AuthorisationHttpHeader, header)
    ()
  }
}

object HawkSignatureCalculator {
  def signatureCalculator(credentials: Credentials, contentType: ContentType): SignatureCalculator =
    new HawkSignatureCalculator(credentials, contentType)
}
