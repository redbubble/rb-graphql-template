package com.redbubble.util.http

import com.twitter.finagle.http.Status.NotFound

// TODO This is a cats.Monoid, replace it with that. Looks like we might need a Semigroup[T], and then pass the
// monoid down into handle404AsEmpty.
trait Empty[T] {
  def empty: T
}

object EmptyInstances {
  def optionEmpty[T]: Empty[Option[T]] = new Empty[Option[T]] {
    override def empty = None
  }

  def seqEmpty[T]: Empty[Seq[T]] = new Empty[Seq[T]] {
    override def empty = Seq.empty[T]
  }
}

trait BackendOps {
  /**
    * Handles a failing 404 response by transforming it into a successful result with an empty value.
    */
  def handle404AsEmpty[T](response: DownstreamResponse[T])(implicit m: Empty[T]): DownstreamResponse[T] = {
    response.map { either =>
      either.fold(
        {
          case DownstreamError(_, i) if i.response.exists(r => r.response.status == NotFound) => Right(m.empty)
          case error => Left(error)
        },
        ws => Right(ws)
      )
    }
  }
}

object BackendOps extends BackendOps
