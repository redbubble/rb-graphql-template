package com.redbubble.util.http

import com.redbubble.util.http.BackendOps.handle404AsEmpty
import com.redbubble.util.http.EmptyInstances.{optionEmpty, seqEmpty}

package object syntax {

  implicit final class RequestSeqSyntax[A](val r: DownstreamResponse[Seq[A]]) extends AnyVal {
    def empty: DownstreamResponse[Seq[A]] = handle404AsEmpty(r)(seqEmpty)
  }

  implicit final class RequestOptionSyntax[A](val r: DownstreamResponse[Option[A]]) extends AnyVal {
    def empty: DownstreamResponse[Option[A]] = handle404AsEmpty(r)(optionEmpty)
  }

}
