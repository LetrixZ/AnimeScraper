package models

import models.enums.ExternalSourceType

@kotlinx.serialization.Serializable
data class ExternalSource(
  val slug: String,
  val source: ExternalSourceType
)

