package com.redbubble.gql.graphql.schema.mutation

import com.redbubble.graphql.SuccessfulGraphQlResult
import com.redbubble.gql.graphql.schema.QueryHelper
import com.redbubble.util.spec.SpecHelper
import org.specs2.mutable.Specification

final class DeviceRegistrationSpec extends Specification with SpecHelper with QueryHelper {

  val registerDeviceMutationWithToken = registerPayload(
    """
      |{
      |    token: "40f4707 bebcf74f 9b7c25d4 8e335894 5f6aa01d a5ddb387 462c7eaf 61bb78ad",
      |    bundleId: "com.redbubble.gql",
      |    appVersion: "1.0.0",
      |    osVersion: "10.0 (14A5309d)"
      |}
    """.stripMargin
  )

  val registerDeviceMutationWithoutToken = registerPayload(
    """
      |{
      |    bundleId: "com.redbubble.gql",
      |    appVersion: "1.0.0",
      |    osVersion: "10.0 (14A5309d)"
      |}
    """.stripMargin
  )

  "Devices" >> {
    "can be registered" >> {
      val result = testQuery(registerDeviceMutationWithToken)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }

    "can be registered without a push token" >> {
      val result = testQuery(registerDeviceMutationWithoutToken)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }
  }

  private def registerPayload(input: String) = {
    s"""
       |mutation RegisterDeviceMutation {
       |  registerDevice(input: $input) {
       |    features {
       |      id
       |      enabled
       |    }
       |    supportStatus {
       |      status
       |      reason
       |    }
       |    device {
       |      app {
       |        bundleId
       |        version {
       |          ...versionDetails
       |        }
       |      }
       |      token
       |      app {
       |        bundleId
       |        version {
       |          ...versionDetails
       |        }
       |      }
       |      osVersion {
       |        ...versionDetails
       |      }
       |    }
       |  }
       |}
       |
        |fragment versionDetails on SoftwareVersion {
       |  major
       |  minor
       |  patch
       |  build
       |  canonical
       |}
      """.stripMargin
  }
}
