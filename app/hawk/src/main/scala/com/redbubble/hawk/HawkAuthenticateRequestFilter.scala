package com.redbubble.hawk

import cats.syntax.either._
import com.redbubble.hawk.HawkAuthenticate.authenticateRequest
import com.redbubble.hawk.RequestContextBuilder.buildContext
import com.redbubble.hawk.validate.Credentials
import com.redbubble.util.http.ResponseOps._
import com.redbubble.util.http.{ApiError, AuthenticationFailedError}
import com.redbubble.util.metrics.StatsReceiver
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future

abstract class HawkAuthenticateRequestFilter(
    credentials: Credentials,
    whitelistedPaths: Seq[String],
    statsReceiver: StatsReceiver)
    extends SimpleFilter[Request, Response] {

  private val stats = statsReceiver.scope("hawk_auth")
  private val failureCounter = stats.counter("failure")
  private val successCounter = stats.counter("success")

  final override def apply(request: Request, continue: Service[Request, Response]): Future[Response] =
    if (whitelistedPaths.exists(p => request.path.startsWith(p))) {
      continue(request)
    } else {
      authenticate(request).fold(
        e => {
          failureCounter.incr()
          unauthorised(e.getMessage)
        },
        _ => {
          successCounter.incr()
          continue(request)
        }
      )
    }

  private def authenticate(request: Request): Either[ApiError, RequestValid] = {
    val valid = buildContext(request).map { context =>
      authenticateRequest(credentials, context)
    }.getOrElse(errorE(s"Missing authentication header '$AuthorisationHttpHeader'"))
    valid.leftMap(e => AuthenticationFailedError("Request is not authorised", Some(e)))
  }
}
