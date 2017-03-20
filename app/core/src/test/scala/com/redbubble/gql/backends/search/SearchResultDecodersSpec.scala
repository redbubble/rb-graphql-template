package com.redbubble.gql.backends.search

import com.redbubble.gql.util.spec.ModelGenerators
import com.redbubble.util.spec.SpecHelper
import org.scalacheck.Prop._
import org.scalacheck.Properties
import org.specs2.mutable.Specification

final class SearchResultDecodersSpec extends Specification with SpecHelper with ModelGenerators with SearchBackendJson {
  "Parsing full search results" >> {
    "returns the right number of results" >> {
      val decoded = decode(searchJson)(SearchResultDecoders.rawSearchResultDataDecoder)
      decoded must beRight
      decoded.map(r => r.products).getOrElse(Nil) must haveSize(30)
    }
  }

  val decodingProp = new Properties("Search results decoding") {
    property("decoding") = forAll(genRawSearchResult) { (searchResult: RawSearchResult) =>
      val decoded = decode(productSearchJson(searchResult))(SearchResultDecoders.rawSearchResultDataDecoder)
      decoded must beRight(searchResult)
    }
  }

  s2"Decoding search results$decodingProp"
}
