package com.redbubble.gql.backends.icarus

import java.net.URL

import com.redbubble.gql.backends.Offset
import com.redbubble.gql.core._
import com.redbubble.gql.magickraum.{WorkImageDefinition, WorkImageRemoteKey}
import com.redbubble.gql.product.Work
import com.redbubble.gql.services.artists.Artist
import com.redbubble.gql.services.product._
import com.redbubble.gql.util.spec.ModelGenerators
import com.redbubble.util.io.Resource.classpathResourceAsBuf
import com.redbubble.util.spec.SpecHelper
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Gen, Properties}
import org.specs2.mutable.Specification

final class IcarusCollectionSpec extends Specification with SpecHelper with ModelGenerators with IcarusBackendJson {
  private implicit val arbString: Arbitrary[String] = Arbitrary(Gen.alphaStr)
  private implicit val arbMrImageUrl = arb590ImageUrl

  val decodingProp = new Properties("Feed decoding") {
    property("decoding") = forAll { (title: WorkTitle, webLink: URL, artist: Artist, newOffset: Int) =>
      val noLocationArtist = artist.copy(location = None)
      val workId = WorkId("123")
      val workImageDefinition = WorkImageDefinition(WorkImageRemoteKey(12345678), workId, IrregularImageSize(PixelWidth(590), PixelHeight(640)))
      val work = Work(workId, title, noLocationArtist, workImageDefinition, webLink)
      val expected = IcarusCollection(Seq(work), Offset(newOffset))
      val json = icarusWorkFeedJson(workId, title, webLink, workImageDefinition, noLocationArtist, Offset(newOffset))
      val decoded = decode(json)(IcarusCollection.icarusCollectionDecoder)
      decoded must beRight(expected)
    }
  }

  s2"Decoding an icarus product feed$decodingProp"

  private lazy val foundFeedJson = classpathResourceAsBuf("/icarus_found_feed.json").get

  "A full Icarus response " >> {
    "can be decoded" >> {
      val decoded = decode(foundFeedJson)(IcarusCollection.icarusCollectionDecoder)
      decoded must beRight { (feed: IcarusCollection) =>
        feed.works must haveSize(24)
        feed.nextOffset must beEqualTo(Offset(29))
      }
    }
  }
}
