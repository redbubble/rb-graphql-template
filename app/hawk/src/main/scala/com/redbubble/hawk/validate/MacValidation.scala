package com.redbubble.hawk.validate

import com.redbubble.hawk.params.ValidatableRequestContext
import com.redbubble.hawk.validate.Maccer.validateRequestMac
import com.redbubble.hawk.{HawkError, ValidationMethod, _}
import mouse.all._

trait MacValid

object MacValidation extends Validator[MacValid] {
  override def validate(credentials: Credentials,
      context: ValidatableRequestContext, method: ValidationMethod): Either[HawkError, MacValid] =
    validateRequestMac(credentials, context, method).map {
      computedMac => validateMac(computedMac, context.clientAuthHeader.mac)
    }.getOrElse(errorE("Request MAC does not match computed MAC"))

  private def validateMac(computedMac: MAC, providedMac: MAC): Either[HawkError, MacValid] =
    (computedMac == providedMac).either(error("Request MAC does not match computed MAC"), new MacValid {})
}
