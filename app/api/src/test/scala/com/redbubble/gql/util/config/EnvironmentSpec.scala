package com.redbubble.gql.util.config

import com.redbubble.gql.util.spec.SpecHelper
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
        development.peopleApiUrl must not(beNull)
      }
    }

    "for test" >> {
      "are valid" >> {
        test.peopleApiUrl must not(beNull)
      }
    }

    "for production" >> {
      "are valid" >> {
        production.peopleApiUrl must not(beNull)
      }
    }
  }
}
