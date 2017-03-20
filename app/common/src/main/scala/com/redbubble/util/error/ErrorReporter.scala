package com.redbubble.util.error

import com.redbubble.util.async.AsyncOps.runAsyncUnit
import com.redbubble.util.config.Environment
import com.redbubble.util.log.UtilsLogger
import com.rollbar.Rollbar
import com.rollbar.payload.data.Level
import com.twitter.util.FuturePool

import scala.collection.JavaConverters._

sealed trait ErrorLevel

case object Debug extends ErrorLevel

case object Info extends ErrorLevel

case object Warning extends ErrorLevel

case object Error extends ErrorLevel

case object Critical extends ErrorLevel

trait ErrorReporter extends UtilsLogger {
  type ExtraData = Map[String, AnyRef]

  /**
    * Note. This should be called on the main thread to have any real effect.
    */
  def registerForUnhandledExceptions(): Unit

  final def debug(t: Throwable, extraData: Option[ExtraData] = None): Unit = {
    log.debug(s"Error: ${t.getMessage}")
    report(Debug, t, extraData)
  }

  final def info(t: Throwable, extraData: Option[ExtraData] = None): Unit = {
    log.info(s"Error: ${t.getMessage}")
    report(Info, t, extraData)
  }

  final def warning(t: Throwable, extraData: Option[ExtraData] = None): Unit = {
    log.warn(s"Error: ${t.getMessage}", t)
    report(Warning, t, extraData)
  }

  final def error(t: Throwable, extraData: Option[ExtraData] = None): Unit = {
    log.error(s"Error: ${t.getMessage}", t)
    report(Error, t, extraData)
  }

  final def critical(t: Throwable, extraData: Option[ExtraData] = None): Unit = {
    log.error(s"Error: ${t.getMessage}", t)
    report(Critical, t, extraData)
  }

  def report(level: ErrorLevel, t: Throwable, extraData: Option[ExtraData]): Unit
}

final class RollbarErrorReporter(accessToken: String, environment: Environment, enabledEnvironments: Seq[Environment])
    (implicit fp: FuturePool) extends ErrorReporter {
  private lazy val rollbar = new Rollbar(accessToken, environment.name)

  override def registerForUnhandledExceptions() =
    environment.runInEnvironment(enabledEnvironments, rollbar.handleUncaughtErrors())

  // See https://rollbar.com/docs/api/items_post/ for more information.
  override def report(level: ErrorLevel, t: Throwable, extraData: Option[ExtraData]) = {
    val reportFunction = runAsyncUnit {
      rollbar.log(t, extraData.map(_.asJava).orNull, levelToRollbarLevel(level))
    }
    environment.runInEnvironment(enabledEnvironments, reportFunction)
  }

  private def levelToRollbarLevel(el: ErrorLevel): Level = el match {
    case Debug => Level.DEBUG
    case Info => Level.INFO
    case Warning => Level.WARNING
    case Error => Level.ERROR
    case Critical => Level.CRITICAL
  }
}
