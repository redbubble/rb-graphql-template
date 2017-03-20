package com.redbubble.gql.backends

import com.redbubble.gql.services.locale.Locale
import com.redbubble.util.http.HttpHeader

object MonolithLocale {
  def monolithLocaleHeaders(locale: Locale): Seq[HttpHeader] = Seq(
    "Country-Code" -> locale.country.isoCode,
    "Currency-Code" -> locale.currency.isoCode,
    "Accept-Language" -> locale.language.isoCode
  ).map(HttpHeader)
}
