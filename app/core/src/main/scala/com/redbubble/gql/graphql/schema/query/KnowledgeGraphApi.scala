package com.redbubble.gql.graphql.schema.query

import com.redbubble.gql.graphql.RootContext
import com.redbubble.gql.graphql.schema.types.LocaleTypes.LanguageCodeArg
import com.redbubble.gql.graphql.schema.types.TopicTypes.{RelatedTopicsType, TopicArg}
import com.redbubble.gql.services.locale.Language
import com.redbubble.util.async.syntax._
import sangria.schema.Field

trait KnowledgeGraphApi {
  final val relatedTopicsField: Field[RootContext, Unit] =
    Field(
      name = "relatedTopics",
      arguments = List(TopicArg, LanguageCodeArg),
      fieldType = RelatedTopicsType,
      description = Some("Information about a topic, including its relevance to other topics. If no topic is sent, default topics will be returned."),
      resolve = ctx => {
        val language = Language(ctx.arg(LanguageCodeArg), None)
        ctx.ctx.relatedTopics(ctx.arg(TopicArg), language).asScala
      }

    )
}
