package com.redbubble.perf.queries

import com.redbubble.gql.services.product._

trait WorkQueries {
  def relatedWorksQuery(workId: WorkId): String =
    s"""
       |{
       |  relatedWorks(workId: "$workId", currency: "AUD", country: "AU") {
       |    title
       |    webLink
       |    id
       |    artist {
       |      ...artistDetails
       |    }
       |    image(width: 1024) {
       |      ...workImageDetails
       |    }
       |  }
       |}
       |
       |fragment workImageDetails on WorkImage {
       |  url
       |  size {
       |    width
       |    height
       |  }
       |}
       |
       |fragment artistDetails on Artist {
       |  name
       |  avatar
       |  id
       |}
     """.stripMargin
}

object WorkQueries extends WorkQueries
