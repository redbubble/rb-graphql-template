package com.redbubble.gql.backends.search

import com.redbubble.gql.backends._
import com.redbubble.gql.product.Product
import com.redbubble.gql.product.ProductDecoders.productDecoder
import com.redbubble.gql.services.search._
import com.redbubble.util.json.syntax._
import io.circe.Decoder

trait SearchResultDecoders {
  private val resultMetadataDecoder: Decoder[ResultMetadata] = Decoder.instance { c =>
    for {
      keywords <- c.downField("raw_query_string").as[String].map(SearchKeywords)
      totalProducts <- c.downField("total_products").as[Int].map(TotalProducts)
      currentPage <- c.downField("current_page").as[Int].map(Page)
      totalPages <- c.downField("total_pages").as[Int].map(TotalPages)
      resultsPerPage <- c.downField("results_per_page").as[Int].map(ResultsPerPage)
    } yield ResultMetadata(keywords, totalProducts, currentPage, totalPages, resultsPerPage)
  }

  private val productsDataDecoder: Decoder[Seq[Product]] = Decoder.instance { c =>
    c.downField("products").as[Seq[Product]](productDecoder.seqDecoder)
  }

  val rawSearchResultDataDecoder: Decoder[RawSearchResult] = Decoder.instance { c =>
    for {
      products <- c.downField("data").as[Seq[Product]](productsDataDecoder)
      metadata <- c.downField("data").as[ResultMetadata](resultMetadataDecoder)
    } yield RawSearchResult(products, metadata)
  }
}

object SearchResultDecoders extends SearchResultDecoders
