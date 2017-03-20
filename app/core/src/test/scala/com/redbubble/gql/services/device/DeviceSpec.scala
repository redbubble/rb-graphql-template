package com.redbubble.gql.services.device

import com.redbubble.util.spec.SpecHelper
import org.specs2.mutable.Specification

final class DeviceSpec extends Specification with SpecHelper {
  val token = PushNotificationToken("40f4707 bebcf74f 9b7c25d4 8e335894 5f6aa01d a5ddb387 462c7eaf 61bb78ad")
  val version = SoftwareVersion(MajorVersion("1"), MinorVersion("0"), PatchVersion("0"), None, RawVersion("1.0.0"))
  val app = App(BundleId("com.redbubble.foo"), version)

  "Devices created with a token" >> {
    "can be created" >> {
      Device.device(Some(token), app, version) must not(beNull)
    }

    "do support notifications" >> {
      Device.device(Some(token), app, version).supportsPushNotifications must beTrue
    }
  }

  "Devices created without a token" >> {
    "are assigned a generated token" >> {
      val device = Device.device(None, app, version)
      device.token.startsWith(Device.generatedPrefix) must beTrue
    }

    "do not support notifications" >> {
      Device.device(None, app, version).supportsPushNotifications must beFalse
    }
  }
}
