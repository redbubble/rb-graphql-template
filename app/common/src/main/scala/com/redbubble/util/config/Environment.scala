package com.redbubble.util.config

/**
  * An "environment" is a set of environment variables & a name.
  */
trait Environment {
  val name: String
  val isDevelopment: Boolean
  val isTest: Boolean
  val isProduction: Boolean

  /**
    * Runs `f` if this environment is in the `applicableEnvironments`. Use this to say things like
    * "only run this in production".
    */
  final def runInEnvironment[A](applicableEnvironments: Seq[Environment], f: => Unit): Unit =
    if (applicableEnvironments.contains(this)) {
      f
    }
}
