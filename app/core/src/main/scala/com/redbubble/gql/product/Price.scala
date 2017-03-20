package com.redbubble.gql.product

import com.redbubble.gql.services.locale.Currency

final case class Price(amount: BigDecimal, currency: Currency)
