package com.redbubble.gql.backends.search

import com.redbubble.gql.backends.{Page, ResultsPerPage, TotalPages, TotalProducts}
import com.redbubble.gql.product.Product
import com.redbubble.gql.services.search.SearchKeywords

final case class ResultMetadata(keywords: SearchKeywords,
    totalProducts: TotalProducts, currentPage: Page, totalPages: TotalPages, resultsPerPage: ResultsPerPage)

final case class RawSearchResult(products: Seq[Product], metadata: ResultMetadata)
