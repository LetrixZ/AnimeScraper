import Utils.get
import Utils.json
import Utils.post
import Utils.put
import data.AnimeData
import data.Matches
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import models.Streaming
import models.enums.AnimeStatus
import models.enums.StreamingType
import sites.JkAnime

val jkAnime = JkAnime()

fun main() {
  jkAnime.getItems()
}

/*
  Checks Streaming sites for recently added items
*/
fun getRecents(): String? {
  val recentItems = runBlocking {
    jkAnime.getRecents()
  }
  val checkResponse = post(json.encodeToString(recentItems), "streaming/check")
  val missingItems: List<Streaming> = json.decodeFromString(checkResponse.body())
  val toInsert: List<Streaming> = runBlocking {
    missingItems.map {
      when (it.source) {
        StreamingType.JKANIME -> return@map jkAnime.getItem(it.slug)
        else -> throw Exception("Invalid source - [${it.source}] ${it.slug}")
      }
    }
  }
  return post(json.encodeToString(toInsert), "streaming").body()
}

/*
  Updates Streaming items with given airing status
*/
fun updateStatus(status: AnimeStatus): String? {
  val url = "streaming/status/${status.name.lowercase()}"
  val items: List<Streaming> = json.decodeFromString(get(url).body())
  val updatedItems = runBlocking {
    items.map {
      when (it.source) {
        StreamingType.JKANIME -> return@map jkAnime.getItem(it.slug)
        else -> throw Exception("Invalid source - [${it.source}] ${it.slug}")
      }
    }
  }
  return put(json.encodeToString(updatedItems), "streaming").body()
}

/*
  Insert Anime items
*/
fun insertAnime(): String? = post(json.encodeToString(AnimeData.manamiToAnime()), "anime").body()

/*
  Insert External sources
*/
fun insertExternalSources(): String? = post(json.encodeToString(AnimeData.manamiToAnime()), "external").body()

/*
  Insert Streaming items
*/
fun insertStreaming(): String? = post(json.encodeToString(jkAnime.getItems()), "streaming").body()

/*
  Insert StreamingExternal items
*/
fun insertStreamingExternal(): String? = post(json.encodeToString(Matches.getMatches()), "streaming-external").body()

/*
  Add Anime ID to Streaming items
*/
fun insertStreamingAnime(): String? = post(json.encodeToString(jkAnime.getItems()), "streaming-anime").body()

