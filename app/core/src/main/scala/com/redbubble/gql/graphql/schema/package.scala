package com.redbubble.gql.graphql

import com.redbubble.gql.graphql.schema.types.InputTypes.{StringKvKeyType, StringKvValueType}
import sangria.marshalling.FromInput.InputObjectResult
import sangria.schema.InputObjectType._
import sangria.util.tag._

package object schema {
  type KvPairInputType = DefaultInput @@ InputObjectResult

  def inputKvPairsToTuples(kvs: Seq[KvPairInputType]): Seq[(String, String)] =
    kvs.flatMap { m =>
      for {
        k <- m.get(StringKvKeyType.name)
        v <- m.get(StringKvValueType.name)
      } yield (k.toString, v.toString)
    }
}
