package com.redbubble.util.error

import com.redbubble.util.log.Logger
import com.twitter.util.Monitor

abstract class ErrorReportingMonitor(logger: Logger, errorReporter: ErrorReporter) extends Monitor {
  private val Handled = true

  /**
    * Handle an error, that we do not handle elsewhere. Errors here usually mean that we have some form of programmer
    * error, within the API, for example a `java.lang.NullPointerException` when actually handling an error.
    */
  override def handle(e: Throwable): Boolean = {
    logUnhandledError(e)
    errorReporter.critical(e)
    Handled
  }

  //noinspection ScalaStyle
  private def logUnhandledError(t: Throwable): Unit =
    try {
      logger.info(s"Unhandled exception with message $t")
    } catch {
      // We do this as this may occur before we've initialised logging.
      case e: Throwable =>
        Console.err.println(s"Unable to log unhandled exception: $e")
        throw e
    }
}
