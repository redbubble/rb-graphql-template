package com.redbubble.util.fetch

import com.redbubble.util.async.AsyncOps.runAsyncUnit
import com.twitter.util._
import fetch.Query.{Callback, Errback}
import fetch.{Ap, Async, FetchMonadError, Query, Sync}
import io.catbird.util.{twitterFutureInstance => futureMonadError}

import scala.concurrent.duration.{FiniteDuration, Duration => ScalaDuration}

object TwitterFutureFetchMonadError {
  def twitterFutureFetchMonadError(implicit fp: FuturePool): FetchMonadError[Future] =
    new TwitterFutureFetchMonadError()(fp)
}

private class TwitterFutureFetchMonadError(implicit futurePool: FuturePool)
    extends FetchMonadError.FromMonadError[Future]()(futureMonadError) {

  override def runQuery[A](q: Query[A]): Future[A] = queryToFuture(q)

  private def queryToFuture[A](q: Query[A]): Future[A] = q match {
    case Sync(e) => Future.value(e.value)
    case Async(action, timeout) => actionToFuture(action, timeout)
    case Ap(qf, qx) => Future.join(queryToFuture(qf), queryToFuture(qx)).map(tuple => tuple._1(tuple._2))
  }

  private def actionToFuture[A](action: (Callback[A], Errback) => Unit, timeout: ScalaDuration): Future[A] = {
    val p = new Promise[A]()
    runAsyncUnit {
      val success = (a: A) => {
        p.setValue(a)
        ()
      }
      val error = (e: Throwable) => {
        p.setException(e)
        ()
      }
      action(success, error)
    }
    timeout match {
      case _: FiniteDuration => p.raiseWithin(Duration(timeout.length, timeout.unit))(Timer.Nil)
      case _ => p
    }
  }
}
