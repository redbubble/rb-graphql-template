package com.redbubble.gql.auth

import java.net.URL

import com.twitter.finagle.http.Cookie

/**
  * An authenticated user session with the website, embedded in the app as a web view (so needing cookies, etc.).
  */
final case class EmbeddedWebSession(webCheckoutUrl: URL, cookies: Seq[Cookie] = Seq.empty)
