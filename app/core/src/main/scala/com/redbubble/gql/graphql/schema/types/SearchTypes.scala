package com.redbubble.gql.graphql.schema.types

import com.redbubble.graphql.ScalarTypes.stringScalarType
import com.redbubble.gql.services.search.SearchKeywords
import sangria.marshalling.ToInput.ScalarToInput
import sangria.schema.Argument
import sangria.validation.ValueCoercionViolation

object SearchTypes {

  //
  // Search keyword
  //

  private implicit val searchKeywordsInput = new ScalarToInput[SearchKeywords]

  private case object SearchKeywordsCoercionViolation extends ValueCoercionViolation(s"Search keywords expected")

  private def parseSearchKeywords(s: String): Either[SearchKeywordsCoercionViolation.type, SearchKeywords] = Right(SearchKeywords(s))

  val SearchKeywordsType = stringScalarType(
    "SearchKeywords",
    "Keywords to use in a search.",
    parseSearchKeywords, () => SearchKeywordsCoercionViolation
  )

  val SearchKeywordsArg = Argument("keywords", SearchKeywordsType, "Keywords to use in a search.")
}
