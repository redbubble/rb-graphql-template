package com.redbubble.perf.scenarios

import com.redbubble.perf.common.BaseSettings._
import com.redbubble.perf.operations.{DeviceRegistrationOperations, FeedOperations}
import com.redbubble.perf.queries.FeedQueries._
import io.gatling.core.Predef._

import scala.concurrent.duration._

final class AppStartup extends Simulation with FeedOperations with DeviceRegistrationOperations {
  val scn = scenario("App Startup")
      .exec(registerDevice)
      .exec(faturedFeeds)
      .pause(1.seconds)
      .exec(feed(foundFeedCode))
      .pause(1.seconds)

  setUp(
    scn.inject(rampUpUsers)
  ).protocols(jsonHttpProtocol())
}
