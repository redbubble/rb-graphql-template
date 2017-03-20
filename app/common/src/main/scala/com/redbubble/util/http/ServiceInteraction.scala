package com.redbubble.util.http

import java.net.URL

import com.twitter.finagle.http.Response

final case class HttpRequest(url: URL, headers: Seq[HttpHeader], body: Option[String])

final case class HttpResponse(response: Response)

/**
  * A HTTP interaction between a client and a server.
  */
final case class ServiceInteraction(request: HttpRequest, response: Option[HttpResponse]) {
  val requestUrl: String = request.url.toString

  val requestHeaders: String = request.headers.map(t => s"${t._1}=${t._2}").mkString(", ")

  val requestBody: String = request.body.getOrElse("<Empty>")

  val responseStatusCode: String = response.map(_.response.statusCode.toString).getOrElse("<Unknown>")

  val responseBody: String = response.map(_.response.contentString).getOrElse("<Empty>")
}

object ServiceInteraction {
  def interaction(requestUrl: URL, requestHeaders: Seq[HttpHeader],
      requestBody: Option[String], response: Option[Response]): ServiceInteraction =
    ServiceInteraction(HttpRequest(requestUrl, requestHeaders, requestBody), response.map(HttpResponse))
}
