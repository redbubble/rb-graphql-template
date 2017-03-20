package com.redbubble.gql.fetch.works

import cats.data.NonEmptyList
import com.redbubble.gql.backends.works.WorksBackend
import com.redbubble.gql.fetch.DataSourceOps.asyncQuery
import com.redbubble.gql.product.Work
import fetch._

object RelatedWorksDataSource extends DataSource[WorkIdLocaleId, Seq[Work]] {
  override def name = "RelatedWorks"

  override def identity(i: WorkIdLocaleId) = (name, i.identity)

  override def fetchOne(id: WorkIdLocaleId) = asyncQuery(WorksBackend.relatedWorks(id.workId, id.locale))

  override def fetchMany(ids: NonEmptyList[WorkIdLocaleId]) = batchingNotSupported(ids)
}
