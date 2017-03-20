package com.redbubble.gql.fetch.works

import cats.data.NonEmptyList
import com.redbubble.gql.backends.works.WorksBackend
import com.redbubble.gql.fetch.DataSourceOps.asyncQuery
import com.redbubble.gql.product.{Product, Work}
//import com.redbubble.gql.services.product.WorkId
//import com.redbubble.util.async.FutureOps.flattenFuture
import fetch._

object AvailableProductsWithWorkDataSource extends DataSource[WorkLocaleId, (Work, Seq[Product])] {
  override def name = "AvailableProductsWithWork"

  // While we want to reduce single calls, the available products call performs slower when done in a batch (as compared
  // to N concurrent calls). We want a nice balance between speed & not too many calls.
  //override def maxBatchSize = Some(1)

  override def identity(i: WorkLocaleId) = (name, i.identity)

  override def fetchOne(id: WorkLocaleId) = asyncQuery {
    WorksBackend.allProductsForWork(id.work.workId, id.locale).map(e => e.map(ps => (id.work, ps)))
  }

  // Batching is currently slower than concurrent access.
  override def fetchMany(ids: NonEmptyList[WorkLocaleId]) = batchingNotSupported(ids)

//  override def fetchMany(ids: NonEmptyList[WorkLocaleId]) = {
//    // While the works backend supports batching, it supports a single locale for all works.
//    val locale = ids.head.locale
//    Query.async { (ok, error) =>
//      val ff = flattenFuture(WorksBackend.allProductsForWorks(ids.map(_.work.workId).toList, locale))
//      ff.onSuccess(fetchedWps => ok(productsForWorks(ids, fetchedWps))).onFailure(error)
//      ()
//    }
//  }

//  private def productsForWorks(ids: NonEmptyList[WorkLocaleId], fetchedWorkProducts: Map[WorkId, Seq[Product]]) = {
//    ids.foldLeft(Map[WorkLocaleId, (Work, Seq[Product])]()) { (acc, id) =>
//      val fetchedProducts: Option[Seq[Product]] = fetchedWorkProducts.get(id.work.workId)
//      fetchedProducts.fold(acc)(ps => acc + (id -> (id.work -> ps)))
//    }
//  }
}
