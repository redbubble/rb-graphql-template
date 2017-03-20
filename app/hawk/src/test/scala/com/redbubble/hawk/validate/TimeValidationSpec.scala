package com.redbubble.hawk.validate

import com.redbubble.hawk.params._
import com.redbubble.hawk.spec.Generators
import com.redbubble.hawk.util.Time
import com.redbubble.hawk.util.Time._
import com.redbubble.hawk.validate.TimeValidation.{acceptableTimeDelta, validate}
import com.redbubble.hawk.{HeaderValidationMethod, _}
import com.redbubble.util.spec.SpecHelper
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import org.specs2.mutable.Specification

final class TimeValidationSpec extends Specification with SpecHelper with Generators {
  val credentials = Credentials(KeyId("fred"), Key("d0p1h1n5"), Sha256)

  val timestamps = new Properties("Timestamps") {
    property("are valid if within the interval") = forAll { (time: Time) =>
      val delta = nowUtc.minus(time).getStandardSeconds
      if (delta > acceptableTimeDelta.getStandardSeconds) {
        validate(credentials, context(time), HeaderValidationMethod) must beLeft
      } else {
        validate(credentials, context(time), HeaderValidationMethod) must beRight
      }
    }
  }

  s2"Validating timestamps$timestamps"

  private def context(time: Time): ValidatableRequestContext = {
    val header = RequestAuthorisationHeader(
      KeyId("fred"), time, Nonce(Base64Encoded("nonce")), None, Some(ExtendedData("data")), MAC(Base64Encoded("base64")))
    ValidatableRequestContext(RequestContext(Get, Host("example.com"), Port(80), UriPath("/"), None), header)
  }
}
