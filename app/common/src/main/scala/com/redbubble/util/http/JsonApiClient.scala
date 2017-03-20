package com.redbubble.util.http

import java.net.URL

import com.redbubble.util.http.ServiceInteraction._
import com.redbubble.util.json.JsonPrinter.jsonToString
import com.redbubble.util.log.Logger
import com.redbubble.util.metrics.StatsReceiver
import com.twitter.finagle.http.Response
import com.twitter.util.Future
import io.circe.syntax._
import io.circe.{Decoder => CirceDecoder, Encoder => CirceEncoder}
import org.jboss.netty.handler.codec.http.HttpHeaders.Names._

object JsonApiClient {
  final def errorDecoder(url: URL, headers: Seq[HttpHeader],
      body: Option[String], response: Option[Response]): CirceDecoder[Throwable] = CirceDecoder.instance { c =>
    c.downField("error").as[Option[String]].map { (error: Option[String]) =>
      val message = error.fold("Error talking to downstream service")(message => s"Error talking to downstream service: $message")
      Errors.downstreamError(new Exception(message), interaction(url, headers, None, response))
    }
  }

  def client(baseClient: featherbed.Client, userAgent: String, metricsId: String)(
      implicit statsReceiver: StatsReceiver, logger: Logger): JsonApiClient =
    new JsonApiClient_(baseClient, userAgent, metricsId, statsReceiver, logger)
}

/**
  * A simple wrapper around the featherbed HTTP client, with support for Circe codecs. You should use *at most one* of
  * these clients per downstream service, there is no need to create one for every request, i.e. memoise it in a
  * singleton & reuse it (this is due to how Finagle handles queueing/pooling, etc. internally).
  *
  * For more information: http://twitter.github.io/finagle/guide/FAQ.html#how-long-should-my-clients-live
  */
trait JsonApiClient {
  val metricsId: String
  val statsReceiver: StatsReceiver
  val logger: Logger

  /**
    * Perform a GET request for the given path.
    *
    * Requires a Circe decoder in implicit scope for the response of type `R`.
    */
  def get[R: CirceDecoder](
      path: RelativePath,
      queryString: Seq[QueryParam] = Seq.empty,
      headers: Seq[HttpHeader] = Seq.empty
  ): DownstreamResponse[R]

  /**
    * Perform a GET request for the given path. Returns both the decoded response, as well as the raw HTTP response
    * (for pulling out headers, etc.).
    *
    * Requires a Circe decoder in implicit scope for the response of type `R`.
    */
  def getZip[R: CirceDecoder](
      path: RelativePath,
      queryString: Seq[QueryParam] = Seq.empty,
      headers: Seq[HttpHeader] = Seq.empty
  ): Future[(Either[ApiError, R], Response)]

  /**
    * Perform a POST request for the given path.
    *
    * Requires:
    * - a Circe encoder, in implicit scope for the content, of type `C`.
    * - a Circe decoder, in implicit scope for the response type `R`.
    */
  def post[C: CirceEncoder, R: CirceDecoder](
      path: RelativePath,
      content: C,
      queryString: Seq[QueryParam] = Seq.empty,
      headers: Seq[HttpHeader] = Seq.empty
  ): DownstreamResponse[R]

  /**
    * Perform a POST request for the given path. Returns both the decoded response, as well as the raw HTTP response
    * (for pulling out headers, etc.).
    *
    * Requires:
    * - a Circe encoder, in implicit scope for the content, of type `C`.
    * - a Circe decoder, in implicit scope for the response type `R`.
    */
  def postZip[C, R: CirceDecoder](
      path: RelativePath,
      content: C,
      queryString: Seq[QueryParam] = Seq.empty,
      headers: Seq[HttpHeader] = Seq.empty
  )(implicit ce: CirceEncoder[C]): Future[(Either[ApiError, R], Response)]
}

private final class JsonApiClient_(baseClient: featherbed.Client, userAgent: String,
    override val metricsId: String, override val statsReceiver: StatsReceiver, override val logger: Logger)
    extends JsonApiClient with HttpResponseHandler {

  override def get[R: CirceDecoder](
      path: RelativePath, queryString: Seq[QueryParam], headers: Seq[HttpHeader]) =
    getZip[R](path, queryString, headers).map(_._1)

  override def getZip[R: CirceDecoder](
      path: RelativePath, queryString: Seq[QueryParam], headers: Seq[HttpHeader]) = {
    val request = baseClient.get(path.path)
        .withQueryParams(queryString.toList)
        .withHeaders(USER_AGENT -> userAgent)
        .withHeaders(headers: _*)
        .accept("application/json")
    //logger.info(s"GET on ${request.url} with headers: ${request.headers}")
    logger.trace(s"GET on ${request.url} with headers: ${request.headers}")

    implicit val successDecoder = featherbed.circe.circeDecoder
    implicit val errorDecoder = featherbed.circe.circeDecoder(
      JsonApiClient.errorDecoder(request.url, headers, None, None))

    val handledResponse = request.sendZip[Throwable, R]().map { responseTuple =>
      val i = interaction(request.url, headers, None, Some(responseTuple._2))
      handleValidResponse(responseTuple, i)
    }
    handledResponse.rescue(handleInvalidResponse(HttpRequest(request.url, headers, None)))
  }

  override def post[C: CirceEncoder, R: CirceDecoder](
      path: RelativePath, content: C, queryString: Seq[QueryParam], headers: Seq[HttpHeader]) =
    postZip[C, R](path, content, queryString, headers).map(_._1)

  override def postZip[C, R: CirceDecoder](path: RelativePath,
      content: C, queryString: Seq[QueryParam], headers: Seq[HttpHeader])(implicit ce: CirceEncoder[C]) = {

    val request = baseClient.post(path.path)
        .withQueryParams(queryString.toList)
        .withHeaders(USER_AGENT -> userAgent)
        .withHeaders(headers: _*)
        .withContent(content, "application/json")
        .accept("application/json")
    logger.trace(s"POST to ${request.url} with headers: ${request.headers} & content ${content.asJson}")

    val encodedContent = jsonToString(ce(content))

    implicit val contentEncoder = featherbed.circe.circeEncoder
    implicit val successDecoder = featherbed.circe.circeDecoder
    implicit val errorDecoder = featherbed.circe.circeDecoder(
      JsonApiClient.errorDecoder(request.url, headers, Some(encodedContent), None))

    val handledResponse = request.sendZip[Throwable, R]().map { responseTuple =>
      val i = interaction(request.url, headers, Some(encodedContent), Some(responseTuple._2))
      handleValidResponse(responseTuple, i)
    }
    handledResponse.rescue(handleInvalidResponse(HttpRequest(request.url, headers, Some(encodedContent))))
  }
}
