package models

import models.enums.StreamingType

@kotlinx.serialization.Serializable
data class MatchItem(
  val source: StreamingType,
  val slug: String,
  val title: String,
  val externalSources: List<ExternalSource>
)
