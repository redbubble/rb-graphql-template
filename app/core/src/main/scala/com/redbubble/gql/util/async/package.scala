package com.redbubble.gql.util

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors._

import com.redbubble.gql.util.config.Config
import com.redbubble.util.async.AsyncOps._
import com.twitter.util.FuturePool

import scala.concurrent.ExecutionContext

package object async {
  lazy val executorService: ExecutorService = newFixedThreadPool(Config.maxThreadPoolSize)
  lazy val futurePool: FuturePool = FuturePool.interruptible(executorService)
  lazy val globalAsyncExecutionContext: ExecutionContext = scala.concurrent.ExecutionContext.fromExecutor(executorService)

  sys.addShutdownHook(shutdownExecutorService(executorService))
}
