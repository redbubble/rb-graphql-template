package com.redbubble.hawk.params

import com.redbubble.hawk.validate.RequestAuthorisationHeader

final case class UriPath(path: String)

object Host {
  val UnknownHost = Host("")
}

final case class Host(host: String)

final case class Port(port: Int)

/**
  * Information about a request, either incoming (requiring validation) or outgoing (a call made to a downstream server
  * that requires Hawk validation).
  */
final case class RequestContext(method: HttpMethod, host: Host, port: Port, path: UriPath, payload: Option[PayloadContext])

/**
  * An incoming request, that is validatable (i.e. has a Hawk authorisation header).
  */
final case class ValidatableRequestContext(context: RequestContext, clientAuthHeader: RequestAuthorisationHeader)

final case class ValidatableResponseContext(clientAuthHeader: RequestAuthorisationHeader, payload: Option[PayloadContext])
