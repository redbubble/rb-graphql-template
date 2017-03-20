package com.redbubble.gql.graphql.schema.types

import com.redbubble.gql.auth.EmbeddedWebSession
import com.twitter.finagle.http.Cookie
import sangria.schema.{Field, ObjectType, _}

object SessionTypes {
  val HttpCookieType = ObjectType(
    "HttpCookie", "A HTTP cookie",
    fields[Unit, Cookie](
      Field("name", StringType, Some("The name of the cookie."), resolve = _.value.name),
      Field("value", StringType, Some("The value of the cookie."), resolve = _.value.value),
      Field("path", OptionType(StringType), Some("The path the cookie is valid for."), resolve = _.value.path),
      Field("domain", OptionType(StringType), Some("The comain the cookie is valid for."), resolve = _.value.domain),
      Field(
        "comment", OptionType(StringType),
        Some("A description of the purpose of this cookie."),
        resolve = _.value.comment
      ),
      Field(
        "maxAge", OptionType(IntType),
        Some("The cookie's expiration, in seconds in the future (relative to now)."),
        resolve = _.value.maxAge.inSeconds
      ),
      Field(
        "version", OptionType(IntType),
        Some("The version of HTTP state maintenance to which the cookie conforms."),
        resolve = _.value.version
      ),
      Field(
        "httpOnly", OptionType(BooleanType),
        Some("Returns if this cookie cannot be accessed through client side script."),
        resolve = _.value.httpOnly
      ),
      Field("isSecure", OptionType(BooleanType), Some("Only send this cookie over HTTPS."), resolve = _.value.isSecure)
    )
  )

  val SessionType = ObjectType(
    "EmbeddedWebSession",
    "An authenticated user session with the website, embedded in the app as a web view (so needing cookies, etc.).",
    fields[Unit, EmbeddedWebSession](
      Field(
        "webCheckoutUrl", StringType,
        Some("The URL to continue the checkout process (on the Redbubble website). Requires also providing `cookies`."),
        resolve = _.value.webCheckoutUrl.toString
      ),
      Field(
        "cookies", ListType(HttpCookieType),
        Some("The HTTP cookies to present to the website when making a GET to `webCheckoutUrl`."),
        resolve = _.value.cookies
      )
    )
  )
}
