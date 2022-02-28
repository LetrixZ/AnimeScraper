package models

import kotlinx.serialization.json.JsonObject
import models.enums.AnimeSeason
import models.enums.AnimeStatus
import models.enums.AnimeType

@kotlinx.serialization.Serializable
data class ManamiInput(
  var sources: List<String>,
  var title: String,
  var type: String,
  var episodes: Int,
  var status: String,
  var animeSeason: JsonObject,
  var picture: String,
  var thumbnail: String,
  var synonyms: List<String>,
  var relations: List<String>,
  var tags: List<String>,
)

@kotlinx.serialization.Serializable
data class ManamiOutput(
  var title: String,
  val synonyms: List<String>,
  val type: AnimeType,
  val status: AnimeStatus,
  val episodes: Int,
  val season: AnimeSeason,
  val year: Int,
  val picture: String,
  var externalSources: List<ExternalSource> = emptyList()
)