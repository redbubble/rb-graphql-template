package com.redbubble.gql.fetch.works

import cats.data.NonEmptyList
import com.redbubble.gql.backends.works.WorksBackend
import com.redbubble.gql.fetch.DataSourceOps.asyncQuery
import com.redbubble.gql.product.Product
import com.redbubble.util.async.FutureOps.flattenFuture
import fetch._

object AvailableProductsDataSource extends DataSource[WorkIdLocaleId, Seq[Product]] {
  override def name = "AvailableProducts"

  override def identity(i: WorkIdLocaleId) = (name, i.identity)

  override def fetchOne(id: WorkIdLocaleId) = asyncQuery(WorksBackend.allProductsForWork(id.workId, id.locale))

  override def fetchMany(ids: NonEmptyList[WorkIdLocaleId]) = {
    // While the works backend supports batching, it supports a single locale for all works.
    val locale = ids.head.locale
    Query.async { (ok, error) =>
      val ff = flattenFuture(WorksBackend.allProductsForWorks(ids.map(_.workId).toList, locale))
      ff.onSuccess { ps =>
        val result = ps.map { case (workId, products) => WorkIdLocaleId(workId, locale) -> products }
        ok(result)
      }.onFailure(error)
      ()
    }
  }
}
