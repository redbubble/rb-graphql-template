package com.redbubble.perf.queries

trait DeviceQueries {
  val deviceRegistrationQuery: String =
    s"""
       |mutation {
       |  registerDevice(input: {
       |      token: "40f4707 bebcf74f 9b7c25d4 8e335894 5f6aa01d a5ddb387 462c7eaf 61bb78ad",
       |      bundleId: "com.redbubble.gql",
       |      appVersion: "1.0.0",
       |      osVersion: "10.0 (14A5309d)"
       |    }) {
       |    id
       |    supportStatus {
       |      status
       |      reason
       |    }
       |  }
       |}
     """.stripMargin
}

object DeviceQueries extends DeviceQueries
