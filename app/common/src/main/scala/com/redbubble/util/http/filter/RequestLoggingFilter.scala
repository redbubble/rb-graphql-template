package com.redbubble.util.http.filter

import com.redbubble.util.async.singleThreadedFuturePool
import com.redbubble.util.log.Logger
import com.twitter.finagle.filter.{LogFormatter => FinagleLogFormatter}
import com.twitter.finagle.http.Status.InternalServerError
import com.twitter.finagle.http.filter.{CommonLogFormatter => FinagleCommonLogFormatter}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util._

/**
  * Request logging filter.
  *
  * If you have filters in the chain that modify status codes, you should place this filter before them, otherwise
  * the status codes logged will not be correct (they will be the pre-modified ones).
  *
  * Stolen from com.twitter.finagle.http.filter.LoggingFilter & adapted to SLF4J.
  *
  * Logs are sent in Apache-style: http://httpd.apache.org/docs/2.0/logs.html
  *
  * Apache common log format is: "%h %l %u %t \"%r\" %>s %b"
  * %h: remote host
  * %l: remote logname
  * %u: remote user
  * %t: time request was received
  * %r: request time
  * %s: status
  * %b: bytes
  *
  * We add:
  * %D: response time in milliseconds
  * "%{User-Agent}i": user agent
  */
abstract class RequestLoggingFilter[REQ <: Request](val log: Logger, val formatter: FinagleLogFormatter[REQ, Response])
    extends SimpleFilter[REQ, Response] {

  final def apply(request: REQ, service: Service[REQ, Response]): Future[Response] = {
    //log.info(s">>> RAW REQUEST: '${request.getContentString()}'")
    val elapsed = Stopwatch.start()
    val future = service(request)
    future.respond {
      case Return(response) => logSuccess(elapsed(), request, response)
      case Throw(error) => logException(elapsed(), request, error)
    }
    future
  }

  final def logSuccess(replyTime: Duration, request: REQ, response: Response): Unit = {
    val line = formatter.format(request, response, replyTime)
    log.info(line)
  }

  final def logException(duration: Duration, request: REQ, error: Throwable): Unit = {
    val response = Response(request.version, InternalServerError)
    val line = formatter.format(request, response, duration)
    log.info(line)
  }
}

object RequestLoggingFilter
    extends RequestLoggingFilter[Request](new Logger("access")(singleThreadedFuturePool), new FinagleCommonLogFormatter)

