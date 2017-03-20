package com.redbubble.hawk.validate

import com.redbubble.hawk._
import com.redbubble.hawk.params.PayloadHash

final case class ServerAuthorisationHeader(mac: MAC, payloadHash: PayloadHash, extendedData: ExtendedData)
