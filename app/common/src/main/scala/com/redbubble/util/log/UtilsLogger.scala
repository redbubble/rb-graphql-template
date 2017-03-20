package com.redbubble.util.log

import com.redbubble.util.async.singleThreadedFuturePool

trait UtilsLogger {
  final lazy val log: Logger = new Logger("rb-util")(singleThreadedFuturePool)
}

object UtilsLogger extends UtilsLogger
