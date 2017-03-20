package com.redbubble.util.http

import com.twitter.finagle.IndividualRequestTimeoutException
import com.twitter.finagle.http.{Response, Status}
import com.twitter.finagle.service.{ReqRep, ResponseClass, ResponseClassifier => FinagleResponseClassifier}
import com.twitter.util.{Return, Throw}

trait ResponseClassifier {
  /**
    * A ResponseClassifier that allows individual request timeouts to be retried.
    */
  final val timeoutsRetryable: FinagleResponseClassifier =
    FinagleResponseClassifier.named("TimeoutsRetryable") {
      case ReqRep(_, Throw(_: IndividualRequestTimeoutException)) => ResponseClass.RetryableFailure
    }

  /**
    * A response classifier that marks HTTP 202 status codes as successful. Best when using a retry filter to retry
    * successful, but unfulfilled requests:
    *
    * {{{
    * import com.redbubble.util.http.ResponseClassifier._
    * import import com.twitter.finagle.service.{ResponseClassifier => FinagleResponseClassifier}
    *
    * val twitter = Http.client
    *   .withResponseClassifier(acceptedSuccessful)
    *   .newService("www.redbubble.com")
    * }}}
    */
  final val acceptedSuccessful: FinagleResponseClassifier =
    FinagleResponseClassifier.named("AcceptedSuccessful") {
      case ReqRep(_, Return(r: Response)) if r.status == Status.Accepted => ResponseClass.Success
    }
}

object ResponseClassifier extends ResponseClassifier
