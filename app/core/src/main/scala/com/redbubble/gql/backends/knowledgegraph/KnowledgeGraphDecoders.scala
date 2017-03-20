package com.redbubble.gql.backends.knowledgegraph

import com.redbubble.gql.backends.Topic
import com.redbubble.gql.services.knowledgegraph.{Reformulation, RelatedTopic, RelatedTopics}
import com.redbubble.util.json.DecoderOps
import com.redbubble.util.json.syntax._
import io.circe.Decoder

trait KnowledgeGraphDecoders extends DecoderOps {
  final def reformulationDecoder(baseTopic: Topic): Decoder[Reformulation] = Decoder.instance { c =>
    c.downField("topic").as[String].map(Topic).map(topic => Reformulation(baseTopic, topic))
  }

  final def topicNodeDecoder(baseTopic: Topic): Decoder[RelatedTopic] = Decoder.instance { c =>
    for {
      topic <- c.downField("topic").as[String].map(Topic)
      proximity <- c.downField("proximity_score").as[Double]
    } yield RelatedTopic(topic, proximity)
  }

  final def topicNodesDecoder(baseTopic: Topic): Decoder[RelatedTopics] = Decoder.instance { c =>
    for {
      nodes <- c.downField("nodes").as[Seq[RelatedTopic]](topicNodeDecoder(baseTopic).seqDecoder)
      reformulations <- c.downField("reformulations").as[Seq[Reformulation]](reformulationDecoder(baseTopic).seqDecoder)
    } yield RelatedTopics(baseTopic, nodes, reformulations)
  }
}

object KnowledgeGraphDecoders extends KnowledgeGraphDecoders
