package com.redbubble.gql.backends.works

import com.redbubble.gql.backends.MonolithLocale._
import com.redbubble.gql.config.Environment.env
import com.redbubble.gql.product._
import com.redbubble.gql.services.locale.Locale
import com.redbubble.gql.services.product._
import com.redbubble.gql.util.http.GqlJsonApiClient.client
import com.redbubble.gql.util.metrics.Metrics._
import com.redbubble.util.http.RelativePath._
import com.redbubble.util.http._
import com.redbubble.util.http.syntax._

trait WorksBackend {
  protected val worksApiClient: JsonApiClient

  private implicit val decodeWorks = WorksDecoders.workDataDecoder
  private implicit val decodeProducts = ProductDecoders.productsDataDecoder
  private implicit val decodeProduct = ProductDecoders.productDataDecoder

  final def workDetails(workId: WorkId, locale: Locale): DownstreamResponse[Option[Work]] =
    worksDetails(Seq(workId), locale).map(e => e.map(_.headOption))

  final def worksDetails(workIds: Seq[WorkId], locale: Locale): DownstreamResponse[Seq[Work]] = {
    val workIdsParams = workIds.map(id => "work_ids[]" -> id).map(QueryParam)
    worksApiClient.get[Seq[Work]](emptyPath, workIdsParams, monolithLocaleHeaders(locale)).empty
  }

  final def relatedWorks(workId: WorkId, locale: Locale): DownstreamResponse[Seq[Work]] = {
    val params = Seq("id" -> workId).map(QueryParam)
    worksApiClient.get[Seq[Work]](RelativePath("recommended"), params, monolithLocaleHeaders(locale)).empty
  }

  final def allProductsForWork(workId: WorkId, locale: Locale): DownstreamResponse[Seq[Product]] = {
    val products = allProductsForWorks(Seq(workId), locale)
    products.map(e => e.map { products =>
      products.headOption.map(_._2).getOrElse(Seq.empty)
    })
  }

  final def allProductsForWorks(workIds: Seq[WorkId], locale: Locale): DownstreamResponse[Map[WorkId, Seq[Product]]] =
    productsForWork(workIds, locale)

  final def productDetails(
      workId: WorkId, config: SelectedProductConfig, locale: Locale): DownstreamResponse[Option[Product]] = {
    val params = Seq(
      "work_id" -> workId,
      "ia_code" -> config.typeId,
      "quantity" -> "1"
    ) ++ config.selectedConfig
    worksApiClient.get[Option[Product]](
      RelativePath(s"$workId/product_details"), params.map(QueryParam), monolithLocaleHeaders(locale)).empty
  }

  // Note. This returns all products for a work, including those that we may not support (see SupportedProduct#supportedProducts).
  private def productsForWork(workIds: Seq[WorkId], locale: Locale): DownstreamResponse[Map[WorkId, Seq[Product]]] = {
    val workIdsParams = workIds.map(id => "work_ids[]" -> id).map(QueryParam)
    val response = worksApiClient.get[Seq[Product]](
      RelativePath("available_products"), workIdsParams, monolithLocaleHeaders(locale)).empty
    response.map(e => e.map(ps => ps.groupBy(_.workId)))
  }
}

object WorksBackend extends WorksBackend {
  protected override val worksApiClient = client(env.paopleApiUrl)(clientMetrics)
}
