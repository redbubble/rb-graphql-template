package com.redbubble.gql.config

import com.redbubble.hawk.validate.{Credentials, Sha256}
import com.redbubble.hawk.{Key, KeyId}
import com.redbubble.util.config.ConfigUtils
import com.redbubble.util.http.filter.BasicAuthCredentials
import com.twitter.conversions.storage._
import com.twitter.conversions.time._
import com.twitter.util.{Duration, StorageUnit}

trait MonitoringConfig extends ConfigUtils {
  final val rollbarAccessKey: String = envVarOrFail("ROLLBAR_ACCESS_KEY")
}

trait SecurityConfig extends ConfigUtils {
  final val basicAuthUsername: String = envVarOrFail("BASIC_AUTH_USERNAME")
  final val basicAuthPassword: String = envVarOrFail("BASIC_AUTH_PASSWORD")
  final val apiKeyId: String = envVarOrFail("API_KEY_ID")
  final val apiKey: String = envVarOrFail("API_KEY")

  // When in production, require users to authenticate using these credentials.
  final val basicAuthCredentials: BasicAuthCredentials = BasicAuthCredentials(basicAuthUsername, basicAuthPassword)

  // Path prefixes which require HTTP basic authentication in production.
  final val basicAuthPaths: Seq[String] = Seq(
    "/v1/explore",
    "/v1/admin"
  )

  // When in production, require clients to authenticate to the API using these credentials.
  final val apiAuthenticationCredentials: Credentials = Credentials(KeyId(apiKeyId), Key(apiKey), Sha256)

  // These paths *do not* require HAWK authentication.
  final val whitelistedAuthPaths: Seq[String] = basicAuthPaths ++ Seq(
    "/v1/health"
  )
}

trait CacheConfig extends ConfigUtils {
  // General purpose cache config
  final val generalCacheSize: Long = 2000L
  final val generalCacheTtl: Duration = 12.hours

  // GraphQL query cache config
  final val graphQlQueryCacheSize: Long = 1000L
  final val graphQlQueryCacheTtl: Duration = 4.hours

  // Fetched object cache config
  final val fetchedObjectCacheSize: Long = 1500L
  final val fetchedObjectCacheTtl: Duration = 4.hours
}

trait SystemConfig extends ConfigUtils {
  // The ID of the service, used in logging, tracing, metrics, etc.
  final val systemId: String = "rb-graphql-template"

  final val systemName: String = "Redbubble GraphQL Template"

  final val coreLoggerName: String = systemId

  final def environment: String = envVarOrFail("ENV")

  final def cookieDomain: String = envVarOrFail("COOKIE_DOMAIN")

  final def listenAddress: String = s":${envVarOrFail("PORT")}"

  // The maximum size of the thread pool used for asynchronous work (outside of the Finagle/Netty request handler).
  final val maxThreadPoolSize: Int = 100

  // Reject requests greater than this size.
  final val maxRequestSize: StorageUnit = 5.megabytes

  // The maximum amount of time a server is allowed to spend handling the incoming request, see `CommonParams#withRequestTimeout`.
  final val requestTimeout: Duration = 60.seconds

  // Service concurrency: https://twitter.github.io/finagle/guide/Servers.html#concurrency-limit
  final val maxConcurrentRequests: Int = 3500
  final val maxWaiters: Int = 1000
}

object Config extends SystemConfig with MonitoringConfig with SecurityConfig with CacheConfig with ConfigUtils
