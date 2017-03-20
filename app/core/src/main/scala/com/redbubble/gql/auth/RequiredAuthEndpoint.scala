package com.redbubble.gql.auth

import com.redbubble.gql.auth.AuthToken._
import com.redbubble.gql.auth.Authenticate._
import com.redbubble.util.http.Errors.authFailedError
import com.twitter.util.Future
import io.finch.Output.payload
import io.finch.{Endpoint, _}

// TODO TJA Abstract these & move to utils, e.g. final def optionalAuthorise[T](auth: AuthToken => T): Endpoint[Option[T]]
trait OptionalAuthEndpoint {
  /**
    * An endpoint that pulls authentication information out of the request, and won't fail if it isn't present. You
    * might use this in an idempotent sign in endpoint for example.
    *
    * If successful, will pass an `Option[AuthenticatedClient]` to the block:
    *
    * def hello: Endpoint[Hello] =
    * get("v1" :: "hello" :: string("name") :: optionalAuthorise) { (name: String, c: Option[AuthenticatedClient]) =>
    * Ok(Hello(name))
    * }
    */
  final val optionalAuthorise: Endpoint[Option[AuthenticatedUser]] =
    headerOption(httpAuthTokenHeader).mapOutputAsync { maybeToken =>
      Future.value(payload(maybeToken.map(t => AuthenticatedUser(authToken(t)))))
    }
}

object OptionalAuthEndpoint extends OptionalAuthEndpoint

trait RequiredAuthEndpoint {
  /**
    * An endpoint that fails if an authentication token is not provided in the request headers. If successful, will pass
    * an `AuthenticatedClient` to the block:
    *
    * def hello: Endpoint[Hello] =
    * get("v1" :: "hello" :: string("name") :: authorise) { (name: String, c: AuthenticatedClient) =>
    * Ok(Hello(name))
    * }
    */
  final val authorise: Endpoint[AuthenticatedUser] =
    headerOption(httpAuthTokenHeader).mapOutputAsync { maybeToken =>
      maybeToken.map(t => AuthenticatedUser(authToken(t))) match {
        case Some(c) => authorised(c)
        case None => unauthorized
      }
    }

  private def authorised(c: AuthenticatedUser): Future[Output[AuthenticatedUser]] = Future.value(payload(c))

  private def unauthorized: Future[Output[AuthenticatedUser]] =
    Future.value(Unauthorized(authFailedError(s"Missing auth token; include header '$httpAuthTokenHeader'")))
}

object RequiredAuthEndpoint extends RequiredAuthEndpoint
