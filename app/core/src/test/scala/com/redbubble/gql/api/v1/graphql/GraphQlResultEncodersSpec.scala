package com.redbubble.gql.api.v1.graphql

import com.redbubble.graphql.SuccessfulGraphQlResult
import com.redbubble.gql.util.spec.ModelGenerators
import com.redbubble.util.http.ResponseOps
import com.redbubble.util.spec.SpecHelper
import io.circe.Json
import io.circe.syntax._
import org.scalacheck.Prop._
import org.scalacheck.Properties
import org.specs2.mutable.Specification

final class GraphQlResultEncodersSpec extends Specification with SpecHelper with ModelGenerators {
  implicit val resultEncoder = GraphQlEncoders.graphQlResultEncoder

  val encodeResultProp = new Properties("GraphQlResult JSON encoding") {
    property("encode just returns the contained JSON") = forAll(genJson) { (j: Json) =>
      SuccessfulGraphQlResult(j).underlying.asJson must beEqualTo(j)
    }
  }

  s2"A GraphQlResult can be encoded into JSON$encodeResultProp"

  implicit val encode = GraphQlEncoders.graphQlResultEncode

  val encodeResponseProp = new Properties("GraphQlResult response encoding") {
    property("encode returns the contained JSON unmodified") = forAll(genJson) { (j: Json) =>
      parse(ResponseOps.jsonBuf(j)) must beRight(j)
    }
  }

  s2"A GraphQL result can be turned into a JSON response$encodeResponseProp"
}
