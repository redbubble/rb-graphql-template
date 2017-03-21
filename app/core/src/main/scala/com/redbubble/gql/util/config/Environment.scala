package com.redbubble.gql.util.config

import java.net.URL

import com.redbubble.gql.util.config.Environment.canonicalUrl
import com.redbubble.util.config.{ConfigUtils, Environment => Env}

object Environment extends ConfigUtils {
  final def env: GqlEnvironment = Config.environment match {
    case "development" => Development
    case "test" => Test
    case "production" => Production
    case e => sys.error(s"Unknown environment '$e'")
  }

  final def canonicalUrl(b: String): String = b.replaceAll("/+$", "")
}

trait StarWarsApiEnvironment {
  protected val starWarsApiBaseUrl: String

  // Note. These are lazy as starWarsApiBaseUrl is inherited & these are thus not available when this class is instantiated.
  lazy val peopleApiUrl: URL = new URL(s"${canonicalUrl(starWarsApiBaseUrl)}/api/people/")
}

trait GqlEnvironment extends Env with StarWarsApiEnvironment

case object Development extends GqlEnvironment {
  override val name = "development"
  override val isDevelopment = true
  override val isTest = false
  override val isProduction = false

  //override val starWarsApiBaseUrl = "http://localhost:5000"
  override val starWarsApiBaseUrl = "http://swapi.co"
}

case object Test extends GqlEnvironment {
  override val name = "test"
  override val isDevelopment = false
  override val isTest = true
  override val isProduction = false

  override val starWarsApiBaseUrl = "http://swapi.co"
}

case object Production extends GqlEnvironment {
  override val name = "production"
  override val isDevelopment = false
  override val isTest = false
  override val isProduction = true

  override val starWarsApiBaseUrl = "http://swapi.co"
}
