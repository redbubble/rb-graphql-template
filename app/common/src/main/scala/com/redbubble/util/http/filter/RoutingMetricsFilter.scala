/*
 * Copyright (c) 2014 Ben Whitehead.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redbubble.util.http.filter

import com.redbubble.util.metrics.StatsReceiver
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.stats.Stat
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future

/**
  * Record metrics for request paths & methods, useful for seeing which Finch endpoints are being utilised.
  *
  * From: https://github.com/BenWhitehead/finch-server/blob/master/src/main/scala/io/github/benwhitehead/finch/filters/StatsFilter.scala
  *
  * @param statsReceiver Where to send metrics to.
  */
abstract class RoutingMetricsFilter(statsReceiver: StatsReceiver) extends SimpleFilter[Request, Response] {
  private val stats = statsReceiver.scope("route")

  final override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    val label = s"${request.method.toString.toLowerCase}.root.${request.path.stripPrefix("/")}".replace("/", "_")
    Stat.timeFuture(stats.stat("stat", label)) {
      val f = service(request)
      stats.counter("count", label).incr()
      f
    }
  }
}
