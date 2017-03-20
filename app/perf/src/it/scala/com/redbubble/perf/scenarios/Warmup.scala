package com.redbubble.perf.scenarios

import com.redbubble.gql.services.product._
import com.redbubble.perf.common.BaseSettings._
import com.redbubble.perf.operations.{DeviceRegistrationOperations, FeedOperations, ProductOperations}
import com.redbubble.perf.queries.FeedQueries._
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration._

// Warms up the remote server. Run this to warm the caches and the JVMs; or to get a baseline for a non-loaded server.
final class Warmup extends Simulation with ProductOperations with FeedOperations with DeviceRegistrationOperations {
  val scn: ScenarioBuilder = scenario("Warmup")
      .exec(registerDevice)
      .pause(1.seconds)
      .exec(faturedFeeds)
      .pause(1.seconds)
      .exec(feed(foundFeedCode))
      .pause(1.seconds)
      .exec(availableProducts(WorkId("21461273")))
      .pause(1.seconds)

  setUp(
    scn.inject(constantUsersPerSec(1) during 60.seconds)
  ).protocols(jsonHttpProtocol())
}
