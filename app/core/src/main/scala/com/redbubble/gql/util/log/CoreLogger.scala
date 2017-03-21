package com.redbubble.gql.util.log

import com.redbubble.gql.util.config.Config
import com.redbubble.gql.util.async.futurePool
import com.redbubble.util.log.Logger

trait CoreLogger {
  final lazy val log = new Logger(Config.coreLoggerName)(futurePool)
}

object CoreLogger extends CoreLogger
