package com.redbubble.perf.common

import com.redbubble.hawk.params.ContentType
import com.redbubble.gql.config.Config.apiAuthenticationCredentials
import com.redbubble.perf.common.HawkSignatureCalculator.signatureCalculator
import com.redbubble.perf.common.TestEnvironment._
import io.gatling.core.Predef._
import io.gatling.core.controller.inject.RampInjection
import io.gatling.core.session.StaticStringExpression
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._

trait BaseSettings {
  private val JsonContentType = ContentType("application/json")
  private val UserAgent = StaticStringExpression("GatlingLoadTester")
  //private val defaultEnvironment = productionTestEnvironment
  private val defaultEnvironment = localTestEnvironment

  final def jsonHttpProtocol(env: TestEnvironment = defaultEnvironment): HttpProtocolBuilder = {
    val hawkCalculator = signatureCalculator(apiAuthenticationCredentials, JsonContentType)
    http.baseURL(env.baseUrl).disableCaching.signatureCalculator(hawkCalculator).userAgentHeader(UserAgent)
  }

  // 10,000 over 1 minute is about the most we can handle locally.
  //final val rampUpUsers = rampUsers(10000) over 60.seconds
  final val rampUpUsers: RampInjection = rampUsers(3000) over 60.seconds
}

object BaseSettings extends BaseSettings
