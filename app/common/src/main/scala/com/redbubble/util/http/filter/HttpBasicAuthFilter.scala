package com.redbubble.util.http.filter

import com.redbubble.util.http.ResponseOps
import com.redbubble.util.metrics.StatsReceiver
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util._

final case class AuthScheme(scheme: String, credentials: String)

final case class BasicAuthCredentials(username: String, password: String)

/**
  * A filter that performs HTTP basic authentication, https://en.wikipedia.org/wiki/Basic_access_authentication.
  *
  * TODO Replace with https://github.com/finagle/finagle-http-auth
  *
  * @param realm              The security realm to use.
  * @param credentials        The credentials to use to secure the service.
  * @param authenticatedPaths Which path prefixes to authenticate.
  * @param statsReceiver      Where to send metrics to.
  */
abstract class HttpBasicAuthFilter(
    realm: String,
    credentials: BasicAuthCredentials,
    authenticatedPaths: Seq[String],
    statsReceiver: StatsReceiver)
    extends SimpleFilter[Request, Response] {

  private val stats = statsReceiver.scope("basic_auth")
  private val failureCounter = stats.counter("failure")
  private val successCounter = stats.counter("success")

  final override def apply(request: Request, continue: Service[Request, Response]): Future[Response] = {
    if (authenticatedPaths.exists(p => request.path.startsWith(p))) {
      authenticateRequest(request, continue)
    } else {
      continue(request)
    }
  }

  private def authenticateRequest(request: Request, continue: Service[Request, Response]): Future[Response] = {
    val authenticated =
      request.authorization.flatMap(parseauthorizationHeader).map(userCreds => userCreds == credentials)
    authenticated.fold {
      failureCounter.incr()
      basicAuthFailed
    } { _ =>
      successCounter.incr()
      continue(request)
    }
  }

  private def parseauthorizationHeader(authHeader: String): Option[BasicAuthCredentials] =
    for {
      authScheme <- authScheme(authHeader)
      if authScheme.scheme == "basic"
      creds <- basicAuthCredentials(authScheme.credentials)
    } yield creds

  private def authScheme(authorization: String): Option[AuthScheme] =
    authorization.split(" ", 2) match {
      case Array(scheme, params) => Some(AuthScheme(scheme.toLowerCase, params))
      case _ => None
    }

  private def basicAuthCredentials(credentials: String): Option[BasicAuthCredentials] = {
    val decodedParams = new String(Base64StringEncoder.decode(credentials))
    decodedParams.split(":", 2) match {
      case Array(username, password) => Some(BasicAuthCredentials(username, password))
      case _ => None
    }
  }

  private def basicAuthFailed: Future[Response] = {
    ResponseOps.unauthorised("Wrong credentials").map { r =>
      r.headerMap.set("WWW-Authenticate", s"""Basic realm="$realm"""")
      r
    }
  }
}
