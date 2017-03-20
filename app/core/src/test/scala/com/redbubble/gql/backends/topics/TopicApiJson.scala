package com.redbubble.gql.backends.topics

import com.redbubble.gql.backends.Topic

trait TopicApiJson {
  final def topicJson(topic: Topic): String =
    s"""
       |"$topic"
    """.stripMargin

  def topicsJson(topics: Seq[Topic]): String = {
    s"""
       |{
       |  "data": {
       |    "topics": [
       |      ${topics.map(topicJson).mkString(",")}
       |    ]
       |  }
       |}
     """.stripMargin
  }
}
