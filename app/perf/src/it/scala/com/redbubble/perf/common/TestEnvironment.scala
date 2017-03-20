package com.redbubble.perf.common

sealed trait TestEnvironment {
  private final val versionPath: String = "/v1"

  final def baseUrl: String = s"$host$versionPath"

  def host: String
}

private case object LocalTestEnvironment extends TestEnvironment {
  override val host = "http://localhost:8080"
}

private case object ProductionTestEnvironment extends TestEnvironment {
  override val host = "https://rb-graphql-template.redbubble.com"
}

object TestEnvironment {
  val localTestEnvironment: TestEnvironment = LocalTestEnvironment
  val productionTestEnvironment: TestEnvironment = ProductionTestEnvironment
}
