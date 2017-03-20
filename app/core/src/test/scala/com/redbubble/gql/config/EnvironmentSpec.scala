package com.redbubble.gql.config

import com.redbubble.util.spec.SpecHelper
import org.specs2.mutable.Specification

final class EnvironmentSpec extends Specification with SpecHelper {
  val development = Development
  val test = Test
  val production = Production

  "When running tests" >> {
    "the environment is 'test'" >> {
      Environment.env.name must beEqualTo("test")
      Environment.env.isTest must beTrue
    }
  }

  "Environment details" >> {
    "development" >> {
      development.name mustEqual "development"
      development.isDevelopment must beTrue
      development.isTest must beFalse
      development.isProduction must beFalse
    }

    "test" >> {
      test.name mustEqual "test"
      test.isDevelopment must beFalse
      test.isTest must beTrue
      test.isProduction must beFalse
    }

    "production" >> {
      production.name mustEqual "production"
      production.isDevelopment must beFalse
      production.isTest must beFalse
      production.isProduction must beTrue
    }
  }

  // We instantiate these to ensure they are valid URLs (they're lazy)
  "Backend URLs" >> {
    "for development" >> {
      "are valid" >> {
        development.paopleApiUrl must not(beNull)
        development.artistsApiUrl must not(beNull)
        development.cartApiUrl must not(beNull)
        development.localesApiUrl must not(beNull)
        development.searchApiUrl must not(beNull)
        development.collectionsApiUrl must not(beNull)
        development.deliveryDatesApiUrl must not(beNull)
      }
    }

    "for test" >> {
      "are valid" >> {
        test.paopleApiUrl must not(beNull)
        test.artistsApiUrl must not(beNull)
        test.cartApiUrl must not(beNull)
        test.localesApiUrl must not(beNull)
        test.searchApiUrl must not(beNull)
        test.collectionsApiUrl must not(beNull)
        test.deliveryDatesApiUrl must not(beNull)
      }
    }

    "for production" >> {
      "are valid" >> {
        production.paopleApiUrl must not(beNull)
        production.artistsApiUrl must not(beNull)
        production.cartApiUrl must not(beNull)
        production.localesApiUrl must not(beNull)
        production.searchApiUrl must not(beNull)
        production.collectionsApiUrl must not(beNull)
        production.deliveryDatesApiUrl must not(beNull)
      }
    }
  }
}
