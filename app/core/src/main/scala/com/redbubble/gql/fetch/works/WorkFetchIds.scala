package com.redbubble.gql.fetch.works

import com.redbubble.gql.fetch.CommonFetchIdKeys._
import com.redbubble.gql.product.Work
import com.redbubble.gql.services.locale.Locale
import com.redbubble.gql.services.product.WorkId

final case class WorkLocaleId(work: Work, locale: Locale) {
  val identity: String = s"WorkLocaleId-${workKey(work.workId)}-${localeKey(locale)}"
}

final case class WorkIdLocaleId(workId: WorkId, locale: Locale) {
  val identity: String = s"WorkLocaleId-${workKey(workId)}-${localeKey(locale)}"
}
