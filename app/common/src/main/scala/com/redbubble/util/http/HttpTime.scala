package com.redbubble.util.http

import java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME
import java.time.{Instant, ZoneOffset}

object HttpTime {
  private val formatter = RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC)

  //noinspection ScalaStyle
  @volatile private var last: (Long, String) = (0L, "")

  def currentTime(): String = {
    val time = System.currentTimeMillis()
    if (time - last._1 > 1000) {
      last = time -> formatter.format(Instant.ofEpochMilli(time))
      last._2
    } else {
      last._2
    }
  }
}
