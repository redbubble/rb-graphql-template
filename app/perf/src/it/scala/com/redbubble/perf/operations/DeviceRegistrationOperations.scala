package com.redbubble.perf.operations

import com.redbubble.perf.common.Graphql._
import com.redbubble.perf.queries.DeviceQueries.deviceRegistrationQuery
import io.gatling.core.Predef._

trait DeviceRegistrationOperations {
  val registerDevice = {
    exec(
      graphqlRequest("Register Device", graphQlQueryBody(deviceRegistrationQuery))
    )
  }
}
