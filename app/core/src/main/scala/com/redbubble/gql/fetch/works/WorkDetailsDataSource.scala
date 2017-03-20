package com.redbubble.gql.fetch.works

import cats.data.NonEmptyList
import com.redbubble.gql.backends.works.WorksBackend
import com.redbubble.gql.fetch.DataSourceOps._
import com.redbubble.gql.product.Work
import com.redbubble.util.async.FutureOps.flattenFuture
import fetch._

object WorkDetailsDataSource extends DataSource[WorkIdLocaleId, Option[Work]] {
  override def name = "WorkDetails"

  override def identity(i: WorkIdLocaleId) = (name, i.identity)

  override def fetchOne(id: WorkIdLocaleId) = asyncQuery(WorksBackend.workDetails(id.workId, id.locale))

  override def fetchMany(ids: NonEmptyList[WorkIdLocaleId]) = {
    // While the works backend supports batching, it supports a single locale for all works.
    val locale = ids.head.locale
    Query.async { (ok, error) =>
      val ff = flattenFuture(WorksBackend.worksDetails(ids.map(_.workId).toList, locale))
      ff.onSuccess { works =>
        val result = works.foldLeft(Map[WorkIdLocaleId, Option[Work]]()) { (acc, work) =>
          acc + (WorkIdLocaleId(work.workId, locale) -> Some(work))
        }
        ok(result)
      }.onFailure(error)
      ()
    }
  }
}
