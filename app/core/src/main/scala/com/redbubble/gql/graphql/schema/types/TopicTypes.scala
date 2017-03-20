package com.redbubble.gql.graphql.schema.types

import com.redbubble.graphql.ScalarTypes.stringScalarType
import com.redbubble.gql.backends.Topic
import com.redbubble.gql.services.knowledgegraph.RelatedTopic.NoTopic
import com.redbubble.gql.services.knowledgegraph.{Reformulation, RelatedTopic, RelatedTopics}
import sangria.marshalling.ToInput.ScalarToInput
import sangria.schema._
import sangria.validation.ValueCoercionViolation

object TopicTypes {

  //
  // Topic
  //

  private implicit val topicInput = new ScalarToInput[Topic]

  private case object TopicCoercionViolation extends ValueCoercionViolation(s"Topic name expected.")

  private def parseTopic(s: String): Either[TopicCoercionViolation.type, Topic] = Right(Topic(s.toLowerCase))

  val TopicType: ScalarType[Topic] = stringScalarType(
    "TopicCode", "A topic to use for searching.",
    parseTopic, () => TopicCoercionViolation
  )

  val TopicArg: Argument[Topic] = Argument(
    name = "topic",
    argumentType = OptionInputType(TopicType),
    description = "A topic to use for searching.",
    defaultValue = NoTopic
  )

  //
  // Types
  //

  val ReformulationType: ObjectType[Unit, Reformulation] = ObjectType(
    "Reformulation", "A reformulation of a topic.",
    fields[Unit, Reformulation](
      Field(
        name = "topic",
        fieldType = TopicType,
        description = Some("The base topic."),
        resolve = _.value.topic
      ),
      Field(
        name = "reformulation",
        fieldType = TopicType,
        description = Some("The reformulation of the base topic."),
        resolve = _.value.reformulation
      )
    )
  )

  val RelatedTopicType: ObjectType[Unit, RelatedTopic] = ObjectType(
    "RelatedTopic", "Information about a topic, including its relevance to other topics.",
    fields[Unit, RelatedTopic](
      Field(
        name = "topic",
        fieldType = TopicType,
        description = Some("The topic of this node."),
        resolve = _.value.topic
      ),
      Field(
        name = "proximity",
        fieldType = FloatType,
        description = Some("A proximity score (0-1) of this topic to the base topic."),
        resolve = _.value.proximity
      )
    )
  )

  val RelatedTopicsType: ObjectType[Unit, RelatedTopics] = ObjectType(
    "RelatedTopics", "Information about a topic, including its relevance to other topics.",
    fields[Unit, RelatedTopics](
      Field(
        name = "baseTopic",
        fieldType = TopicType,
        description = Some("The topic this topic's proximity is based upon."),
        resolve = _.value.baseTopic
      ),
      Field(
        name = "related",
        fieldType = ListType(RelatedTopicType),
        description = Some("Information on related topics."),
        resolve = _.value.related
      ),
      Field(
        name = "reformulations",
        fieldType = ListType(ReformulationType),
        description = Some(""),
        resolve = _.value.reformulations
      )
    )
  )

  val TopicInputType: InputField[Topic] = InputField("topic", TopicType, "A topic")

  val TopicsArg = Argument("topics", ListInputType(TopicType), "A list of topics.")
}
