package com.redbubble.gql.services.works

import com.redbubble.gql.fetch.products.ProductsFetcher.productDetailsFetch
import com.redbubble.gql.fetch.works.WorksFetcher._
import com.redbubble.gql.product.FeaturedProducts.filterFeatured
import com.redbubble.gql.product.SupportedProduct._
import com.redbubble.gql.product._
import com.redbubble.gql.services.locale.Locale
import com.redbubble.gql.services.product._
import com.redbubble.util.fetch.syntax._
import com.twitter.util.Future

trait WorksService {
  private implicit lazy val fetchRunner = com.redbubble.gql.util.fetch.runner

  final def workDetails(workId: WorkId, locale: Locale): Future[Option[Work]] = {
    val fetch = workFetch(workId, locale)
    fetch.runF
  }

  final def relatedWorks(workId: WorkId, locale: Locale): Future[Seq[Work]] = {
    val fetch = relatedWorksFetch(workId, locale)
    fetch.runF
  }

  final def featuredProducts(workId: WorkId, locale: Locale): Future[FeaturedProducts] = {
    val fetch = supportedAvailableProductsFetch(workId, locale).map(ps => filterFeatured(workId, ps))
    fetch.runF
  }

  final def availableProducts(workId: WorkId, locale: Locale): Future[Seq[Product]] = {
    val fetch = supportedAvailableProductsFetch(workId, locale)
    fetch.runF
  }

  final def productDetails(workId: WorkId, config: SelectedProductConfig, locale: Locale): Future[Option[Product]] = {
    val fetch = productDetailsFetch(workId, config, locale).map(p => p.map(addCropBox))
    fetch.runF
  }

  private def supportedAvailableProductsFetch(workId: WorkId, locale: Locale) =
    productsForWorkFetch(workId, locale).map(ps => ps.filter(isSupported).map(addCropBox))
}

object WorksService extends WorksService
