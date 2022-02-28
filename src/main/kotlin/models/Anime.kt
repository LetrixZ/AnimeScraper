package models

import models.enums.AnimeSeason
import models.enums.AnimeStatus
import models.enums.AnimeType

@kotlinx.serialization.Serializable
data class Anime(
  val slug: String,
  val title: String,
  val synonyms: List<String>,
  val type: AnimeType = AnimeType.UNKNOWN,
  val status: AnimeStatus = AnimeStatus.UNKNOWN,
  val season: AnimeSeason = AnimeSeason.UNKNOWN,
  val year: Int,
  val picture: String,
  val externalSources: List<ExternalSource> = emptyList()
)
