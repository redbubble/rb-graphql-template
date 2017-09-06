package com.redbubble.gql.api.v1

import com.redbubble.gql.util.error.GqlErrorReporter.errorReporter
import com.redbubble.gql.util.log.CoreLogger
import com.redbubble.util.http._
import io.finch.Error._
import io.finch._

object GqlErrorHandler extends ErrorHandler(CoreLogger.log, errorReporter) with ResponseOps {
  // If individual endpoints don't handle errors, we handle them here.
  override val apiErrorHandler: PartialFunction[Throwable, Output[Nothing]] = {
    case e: NotPresent => BadRequest(e)
    case e: NotParsed => BadRequest(e)
    case e: NotValid => BadRequest(e)
    case e: NotFoundError => NotFound(e)
    case e: JsonDecodeFailedError => BadRequest(e)
    case e: io.circe.Error => BadRequest(e)
    case e: AuthenticationFailedError => Unauthorized(e)
    case e: Exception => InternalServerError(e)
  }
}
