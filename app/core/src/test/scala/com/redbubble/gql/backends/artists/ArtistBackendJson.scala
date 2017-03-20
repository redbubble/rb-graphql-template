package com.redbubble.gql.backends.artists

import com.redbubble.gql.product.Work
import com.redbubble.gql.services.artists.Artist

trait ArtistBackendJson {
  final def artistDetailsJson(artists: Seq[Artist]): String =
    s"""
       |{
       |	"data": [
       |    ${artists.map(artistDetailJson).mkString(",")}
       |  ]
       |}
     """.stripMargin

  final def artistWorkJson(artist: Artist, works: Seq[Work]): String =
    s"""
       |{
       |  "data": [
       |    ${works.map(workJson).mkString(",")}
       |  ]
       |}
   """.stripMargin

  final def workJson(work: Work): String =
    s"""
       |{
       |  "work_id": ${work.workId},
       |  "work_link": "${work.webLink}",
       |  "title": "${work.title}",
       |  "work_original_image": {
       |    "remote_key": ${work.imageDefinition.remoteKey},
       |    "dimensions": {
       |      "width": ${work.imageDefinition.originalSize.width},
       |      "height": ${work.imageDefinition.originalSize.height}
       |    }
       |  },
       |  "artist": ${artistDetailJson(work.artist)}
       |}
     """.stripMargin

  final def artistDetailJson(artist: Artist): String =
    s"""
       |{
       |  "id": ${artist.id},
       |  "name": "${artist.name}",
       |  "username": "${artist.username}",
       |  "profile_url": "https://www.redbubble.com/people/akwel?ref=artist_title_name",
       |  "avatar": "${artist.avatar.toString}",
       |  "location": "${artist.location.getOrElse("NO LOCATION!")}",
       |  "joined_date": "2008-11-10",
       |  "published_works": 73
       |}
     """.stripMargin
}
