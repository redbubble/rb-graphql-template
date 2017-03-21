package com.redbubble.perf.scenarios

import com.redbubble.gql.services.people.PersonId
import com.redbubble.perf.common.BaseSettings._
import com.redbubble.perf.operations.PeopleOperations
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration._

final class AppStartup extends Simulation with PeopleOperations {
  val scn: ScenarioBuilder = scenario("App Startup")
      .exec(allPeople())
      .pause(1.seconds)
      .exec(personDetails(PersonId(25)))
      .pause(1.seconds)

  setUp(
    scn.inject(rampUpUsers)
  ).protocols(jsonHttpProtocol())
}
