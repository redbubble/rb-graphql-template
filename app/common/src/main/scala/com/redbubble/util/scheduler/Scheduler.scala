package com.redbubble.util.scheduler

import com.twitter.conversions.time._
import com.twitter.finagle.util.HashedWheelTimer
import com.twitter.util.{Duration, Timer, TimerTask}

/**
  * A course grained (second precision) scheduler.
  */
trait Scheduler {
  /**
    * Run `f` every elapsed `period`, starting `period` from now.
    */
  def schedule(period: Duration)(f: => Unit): TimerTask
}

private final class Scheduler_(timer: Timer) extends Scheduler {
  override def schedule(period: Duration)(f: => Unit): TimerTask = timer.schedule(period)(f)
}

object Scheduler {
  private val TickDuration: Duration = 1.second

  /**
    * Obtain the singleton, application-wide scheduler.
    */
  lazy val scheduler: Scheduler = new Scheduler_(HashedWheelTimer(TickDuration))
}
