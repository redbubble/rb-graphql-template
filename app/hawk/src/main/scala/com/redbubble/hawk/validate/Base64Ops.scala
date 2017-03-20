package com.redbubble.hawk.validate

import java.util.Base64

import com.redbubble.hawk._

trait Base64Ops {
  final def base64Encode(data: Array[Byte]): Base64Encoded = Base64Encoded(Base64.getEncoder.encodeToString(data))
}

object Base64Ops extends Base64Ops

