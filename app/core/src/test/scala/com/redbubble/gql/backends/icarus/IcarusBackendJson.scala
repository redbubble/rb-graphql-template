package com.redbubble.gql.backends.icarus

import java.net.URL

import com.redbubble.gql.backends.Offset
import com.redbubble.gql.core
import com.redbubble.gql.core.Id
import com.redbubble.gql.magickraum.WorkImageDefinition
import com.redbubble.gql.product.Price
import com.redbubble.gql.services.artists.Artist
import com.redbubble.gql.services.product._
import com.redbubble.gql.util.spec.ModelGenerators

trait IcarusBackendJson {

  final def icarusWorkFeedJson(workId: WorkId,
      title: WorkTitle, workLink: URL, workImage: WorkImageDefinition, artist: Artist, newOffset: Offset): String = {
    val price = ModelGenerators.sample(ModelGenerators.genPrice)
    val productImageUrl = ModelGenerators.sample(ModelGenerators.genUrl)
    val productLink = ModelGenerators.sample(ModelGenerators.genUrl)
    val productId = ProductId("123-tshirt")
    val offset = Offset(newOffset)
    icarusProductFeedJson(productId, workId, title, workLink, productLink, productImageUrl, workImage, artist, price, offset)
  }

  private def icarusProductFeedJson(productId: ProductId, workId: WorkId, title: WorkTitle, workLink: URL, productLink: URL,
      imageUrl: URL, workImage: WorkImageDefinition, artist: Artist, price: Price, newOffset: Offset): String = {
    s"""
       |{
       |  "data": [
       |    {
       |      "type": "ThrowPillowProduct",
       |      "options": {
       |        "size": "small",
       |        "type": "cover-only"
       |      },
       |      "product_id": "$productId",
       |      "work_id": "$workId",
       |      "type": "TShirtProduct",
       |      "options": {
       |        "style": "mens",
       |        "size": "medium",
       |        "body_color": "white",
       |        "print_location": "front"
       |      },
       |      "link": "$productLink",
       |      "work_link": "$workLink",
       |      "image_url": "${imageUrl.toString}",
       |      "work_url": "${imageUrl.toString}",
       |      "title": "$title",
       |      "user_name": "${artist.username}",
       |      "artist_name": "${artist.name}",
       |      "artist_url": "http://www.redbubble.com/people/pauk?ref=artist_title_name",
       |      "artist_avatar_url": "${artist.avatar.toString}",
       |      "artist_id": ${artist.artistId.getOrElse(Id("58605008"))},
       |      "price": "${price.amount.toString}",
       |      "work_original_image": {
       |        "remote_key": ${workImage.remoteKey},
       |        "dimensions": {
       |          "width": ${workImage.originalSize.width},
       |          "height": ${workImage.originalSize.height}
       |        }
       |      }
       |    }
       |  ],
       |  "new_offset":$newOffset,
       |  "currency": "${price.currency.isoCode}"
       |}
     """.stripMargin
  }
}
