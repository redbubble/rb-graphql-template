package com.redbubble.perf.queries

trait SearchQueries  {
  def searchQuery(keywords: String, offset: Int = 0): String =
  s"""
{\nsearch(currency: "AUD",limit: 20,country: "AU",keywords: "${keywords}",offset: ${offset},) {\nproducts {\nprice {\namount\ncurrency\nprefix\nunit\n}\nimages {\nurl\nsize {\nwidth\nheight\n}\ncropBox {\norigin {\nx\ny\n}\nwidth\nheight\n}\n}\ntypeName\ntypeId\nid\nworkId\nwebLink\ncategories {\nname\n}\ngenericConfig {\navailableColours {\nname\nlabel\nvalue\nvalueDisplayName\ndefaultSelection\nhexValue\n}\navailableSizes {\nname\nlabel\nvalue\nvalueDisplayName\ndefaultSelection\nmeasurement {\n... on ApparelSizeMeasurement {\nlabel\nchest\nlength\n}\n}\n}\naddnConfig {\nname\nlabel\nvalue\nvalueDisplayName\ndefaultSelection\n}\nnonDisplayableConfig {\nname\nlabel\nvalue\nvalueDisplayName\ndefaultSelection\n}\n}\n}\ntotalProducts\npagination {\ncurrentOffset\nnextOffset\nmoreResults\n}\n}\n\n}\n
  """
}

object SearchQueries extends SearchQueries
