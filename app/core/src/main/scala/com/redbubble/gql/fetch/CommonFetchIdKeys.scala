package com.redbubble.gql.fetch

import com.redbubble.gql.graphql.Pagination
import com.redbubble.gql.product.SelectedProductConfig
import com.redbubble.gql.services.locale.Locale
import com.redbubble.gql.services.product.WorkId

object CommonFetchIdKeys {
  def workKey(workId: WorkId): String = workId

  def localeKey(locale: Locale): String =
    s"${locale.country.isoCode}-${locale.currency.isoCode}-${locale.language.isoCode}"

  def paginationKey(pagination: Pagination): String = s"${pagination.limit}-${pagination.currentOffset}"

  def configKey(config: SelectedProductConfig): String =
    s"${config.typeId}-${config.selectedConfig.map(t => s"${t._1}=${t._2}").mkString("-")}"
}
