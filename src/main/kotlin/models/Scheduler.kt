package models

import models.enums.StreamingType

@kotlinx.serialization.Serializable
data class Scheduler(
  val slug: String,
  val source: StreamingType,
  val episode: Int
)
