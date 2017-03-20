package com.redbubble.gql.graphql.schema.query

import com.redbubble.graphql.SuccessfulGraphQlResult
import com.redbubble.gql.graphql.schema.QueryHelper
import com.redbubble.util.spec.SpecHelper
import org.specs2.mutable.Specification

final class LocaleApiSpec extends Specification with SpecHelper with QueryHelper {
  private val deviceLocaleQuery =
    s"""
       |{
       |  deviceLocale(ipAddress: "192.168.1.1") {
       |    country {
       |      $countryField
       |    }
       |    currency {
       |      $currencyFields
       |    }
       |    language {
       |      $languageField
       |    }
       |  }
       |}
    """.stripMargin

  private val localeInformationQuery =
    s"""
       |{
       |  allLocales {
       |    countries {
       |      $countryField
       |    }
       |    currencies {
       |      $currencyFields
       |    }
       |    languages {
       |      $languageField
       |    }
       |  }
       |}
    """.stripMargin

  "Locale queries" >> {
    "a device can find its default locale" >> {
      val result = testQuery(deviceLocaleQuery)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }

    "all locale information can be found" >> {
      val result = testQuery(localeInformationQuery)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }
  }
}
