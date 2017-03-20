package com.redbubble.hawk.validate

import com.redbubble.hawk.params.ValidatableRequestContext
import com.redbubble.hawk.util.Time.nowUtc
import com.redbubble.hawk.{HawkError, ValidationMethod, _}
import mouse.all._
import org.joda.time.Duration

trait TimeValid

object TimeValidation extends Validator[TimeValid] {
  final val acceptableTimeDelta: Duration = Duration.standardMinutes(2)

  override def validate(credentials: Credentials,
      context: ValidatableRequestContext, method: ValidationMethod): Either[HawkError, TimeValid] = {
    val delta = nowUtc.minus(context.clientAuthHeader.timestamp).getStandardSeconds
    (delta <= acceptableTimeDelta.getStandardSeconds).either(error("Timestamp invalid"), new TimeValid {})
  }
}
