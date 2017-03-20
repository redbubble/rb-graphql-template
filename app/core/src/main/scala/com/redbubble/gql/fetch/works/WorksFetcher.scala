package com.redbubble.gql.fetch.works

import com.redbubble.gql.product.{Product, Work}
import com.redbubble.gql.services.locale.Locale
import com.redbubble.gql.services.product.WorkId
import fetch.Fetch

object WorksFetcher {
  def workFetch(workId: WorkId, locale: Locale): Fetch[Option[Work]] = {
    val id = WorkIdLocaleId(workId, locale)
    Fetch(id)(WorkDetailsDataSource)
  }

  def relatedWorksFetch(workId: WorkId, locale: Locale): Fetch[Seq[Work]] = {
    val id = WorkIdLocaleId(workId, locale)
    Fetch(id)(RelatedWorksDataSource)
  }

  def productsForWorkFetch(workId: WorkId, locale: Locale): Fetch[Seq[Product]] = {
    val id = WorkIdLocaleId(workId, locale)
    Fetch(id)(AvailableProductsDataSource)
  }

  def productsForWorkFetch(work: Work, locale: Locale): Fetch[(Work, Seq[Product])] = {
    val id = WorkLocaleId(work, locale)
    Fetch(id)(AvailableProductsWithWorkDataSource)
  }

  def productsForWorksFetch(works: Seq[Work], locale: Locale): Fetch[List[(Work, Seq[Product])]] = {
    val ids = works.map(WorkLocaleId(_, locale))
    val fetches = ids.map(id => productsForWorkFetch(id.work, locale))
    Fetch.sequence(fetches.toList)
  }

  //def productsForWorksFetch(works: Seq[Work], locale: Locale): Fetch[List[(Work, Seq[Product])]] = {
  //  val ids = works.map(WorkLocaleId(_, locale)).toList
  //  ids match {
  //    case Nil => Fetch.pure(Nil)
  //    case h :: t => Fetch.multiple(h, t: _*)(AvailableProductsWithWorkDataSource)
  //  }
  //}
}
