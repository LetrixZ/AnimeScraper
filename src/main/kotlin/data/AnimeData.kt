package data

import Utils
import Utils.toKebabCase
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import models.*
import models.enums.AnimeSeason
import models.enums.AnimeStatus
import models.enums.AnimeType
import models.enums.ExternalSourceType
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.pathString

object AnimeData {

  fun getData(): List<ManamiInput> = Json.decodeFromString(
    Json.encodeToString(
      Json.decodeFromString<JsonElement>(
        File(
          Paths.get(Utils.resourcesPath.pathString, "manami.json").toUri()
        ).readText()
      ).jsonObject["data"]!!
    )
  )

  fun parseData(data: List<ManamiInput> = getData()): List<ManamiOutput> {
    return data.map {
      it.synonyms = it.synonyms.filter { synonym ->
        val match =
          Regex("""[A-z0-9]+|[\u3040-\u309F]+|[\u30A0-\u30FF]+|[ａ-ｚＡ-Ｚ０-９]+""").findAll(synonym).map { it.value }
            .joinToString(" ")
        return@filter match.length > synonym.length * 0.8
      }.sortedBy { synonym ->
        Regex("""[\u3040-\u309F]+|[\u30A0-\u30FF]+|[ａ-ｚＡ-Ｚ０-９]+""").findAll(synonym).map { it.value }
          .joinToString(" ").length * 100 / synonym.length
      }
      if (it.sources.contains("https://myanimelist.net/anime/49926")) {
        it.sources = it.sources.toMutableList().also { list -> list.add("https://anilist.co/anime/129874") }
      }
      val externalSources = it.sources.mapNotNull { source ->
        if (source == "https://anilist.co/anime/129874" || source == "https://myanimelist.net/anime/50773") return@mapNotNull null
        with(source) {
          when {
            contains("anilist") -> ExternalSource(slug = source.split("/")[4], source = ExternalSourceType.ANILIST)
            contains("myanimelist") -> ExternalSource(
              slug = source.split("/")[4], source = ExternalSourceType.MYANIMELIST
            )
            else -> null
          }
        }
      }
      val type: AnimeType = when (it.type) {
        "TV" -> AnimeType.TV
        "MOVIE" -> AnimeType.MOVIE
        "OVA" -> AnimeType.OVA
        "ONA" -> AnimeType.ONA
        "SPECIAL" -> AnimeType.SPECIAL
        else -> AnimeType.UNKNOWN
      }
      val status: AnimeStatus = when (it.status) {
        "ONGOING" -> AnimeStatus.ONGOING
        "FINISHED" -> AnimeStatus.FINISHED
        "UPCOMING" -> AnimeStatus.UPCOMING
        else -> AnimeStatus.UNKNOWN
      }
      val season: AnimeSeason = when (it.animeSeason["season"]!!.jsonPrimitive.content) {
        "FALL" -> AnimeSeason.FALL
        "WINTER" -> AnimeSeason.WINTER
        "SUMMER" -> AnimeSeason.SUMMER
        "SPRING" -> AnimeSeason.SPRING
        else -> AnimeSeason.UNKNOWN
      }
      try {
        return@map ManamiOutput(
          title = it.title,
          synonyms = it.synonyms,
          type = type,
          status = status,
          season = season,
          year = it.animeSeason["year"]?.jsonPrimitive?.intOrNull ?: 0,
          picture = it.picture,
          episodes = it.episodes,
          externalSources = externalSources
        )
      } catch (e: Exception) {
        println(it)
        throw e
      }
    }
  }

  fun filterData(
    data: List<ManamiOutput> = parseData(),
    matches: List<MatchItem> = Matches.getMatches()
  ): List<ManamiOutput> {
    val externalSources = matches.map { it.externalSources }.flatten()
    return data.filter {
      it.externalSources.any { source ->
        externalSources
          .any { externalSource -> externalSource.slug == source.slug && externalSource.source == source.source }
      }
    }
  }

  fun manamiToAnime(data: List<ManamiOutput> = filterData()): List<Anime> {
    val items = mutableListOf<Anime>()
    data.forEach {
      val synonymTest = it.synonyms.filter { synonym ->
        val match = Regex("""[A-z0-9$-/:-?{-~!"^_`\[\] ]+""").findAll(synonym).map { it.value }.joinToString(" ")
        return@filter match.length > synonym.length * 0.8
      }
      var slug: String?
      slug = it.title.toKebabCase()
      var index = 0
      while (items.any { ex -> ex.slug == slug } || slug == null) {
        if (items.firstOrNull { ex -> ex.slug == slug }?.type != AnimeType.TV && it.type == AnimeType.TV) {
          slug = "${it.title} TV".toKebabCase()
          continue
        }
        val synonym = synonymTest.getOrNull(index)
        if (synonym != null) {
          slug = synonym.toKebabCase()
        } else {
          break
        }
        index += 1
      }
      try {
        items.add(
          Anime(
            title = it.title,
            synonyms = it.synonyms,
            slug = slug!!,
            picture = it.picture,
            status = it.status,
            season = it.season,
            type = it.type,
            year = it.year,
            externalSources = it.externalSources
          )
        )
      } catch (e: Exception) {
        println(it)
        throw e
      }
    }
    return items
  }
}
