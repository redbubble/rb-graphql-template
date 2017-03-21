package com.redbubble.util.http

import com.redbubble.util.http.BackendOps.handle404AsEmpty
import com.redbubble.util.http.EmptyInstances.{optionEmpty, seqEmpty}

package object syntax {

  implicit final class RequestSeqSyntax[A](val r: DownstreamResponse[Seq[A]]) extends AnyVal {
    /**
      * Handles a failing 404 response by transforming it into a successful result containing an empty sequence.
      */
    def empty: DownstreamResponse[Seq[A]] = handle404AsEmpty(r)(seqEmpty)
  }

  implicit final class RequestOptionSyntax[A](val r: DownstreamResponse[Option[A]]) extends AnyVal {
    /**
      * Handles a failing 404 response by transforming it into a successful result containing a None.
      */
    def empty: DownstreamResponse[Option[A]] = handle404AsEmpty(r)(optionEmpty)
  }

}
