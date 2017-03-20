package com.redbubble.gql.backends.works

import com.redbubble.gql.product.Work

trait WorkApiJson {
  def feedJson(works: Seq[Work]): String = {
    s"""
       |{
       |  "data": {
       |    "works": [
       |      ${works.map(workJson).mkString(",")}
       |    ]
       |  }
       |}
     """.stripMargin
  }

  def workJson(work: Work): String = {
    s"""
       |{
       |  "work_id": ${work.id},
       |  "work_link":"${work.webLink}",
       |  "title": "${work.title}",
       |  "work_original_image": {
       |    "remote_key": ${work.imageDefinition.remoteKey},
       |    "dimensions": {
       |      "width": ${work.imageDefinition.originalSize.width},
       |      "height": ${work.imageDefinition.originalSize.height}
       |    }
       |  },
       |  "artist": {
       |    "id": ${work.artist.id},
       |    "name": "${work.artist.name}",
       |    "username": "${work.artist.username}",
       |    "profile_url": "http://www.redbubble.com/people/barachan?ref=artist_title_name",
       |    "avatar": "${work.artist.avatar}",
       |    "location": "${work.artist.location.getOrElse("NO LOCATION!")}"
       |  }
       |}
     """.stripMargin
  }
}
