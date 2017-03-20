package com.redbubble.hawk.validate

import com.redbubble.hawk.params.ValidatableRequestContext
import com.redbubble.hawk.{HawkError, ValidationMethod}

trait Validator[T] {
  def validate(credentials: Credentials, context: ValidatableRequestContext, method: ValidationMethod): Either[HawkError, T]
}
