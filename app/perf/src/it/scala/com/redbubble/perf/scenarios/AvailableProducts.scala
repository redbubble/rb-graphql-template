package com.redbubble.perf.scenarios

import com.redbubble.gql.services.product._
import com.redbubble.perf.common.BaseSettings._
import com.redbubble.perf.operations.ProductOperations
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

final class AvailableProducts extends Simulation with ProductOperations {
  val scn: ScenarioBuilder = scenario("Available products for a Work")
      .exec(availableProducts(WorkId("21461273")))

  setUp(
    scn.inject(rampUpUsers)
  ).protocols(jsonHttpProtocol())
}
