package com.redbubble.perf.scenarios

import com.redbubble.gql.services.people.PersonId
import com.redbubble.perf.common.BaseSettings._
import com.redbubble.perf.operations.PeopleOperations
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration._

// Warms up the remote server. Run this to warm the caches and the JVMs; or to get a baseline for a non-loaded server.
final class Warmup extends Simulation with PeopleOperations {
  val scn: ScenarioBuilder = scenario("Warmup")
      .exec(allPeople())
      .pause(1.seconds)
      .exec(personDetails(PersonId(25)))
      .pause(1.seconds)

  setUp(
    scn.inject(constantUsersPerSec(1) during 60.seconds)
  ).protocols(jsonHttpProtocol())
}
