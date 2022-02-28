package sites

import it.skrape.core.htmlDocument
import models.Scheduler
import models.Streaming
import models.enums.AnimeStatus
import models.enums.AnimeType
import models.enums.StreamingType

class JkAnime(
  override val source: StreamingType = StreamingType.JKANIME, override val url: String = "https://jkanime.net"
) : BaseSite() {
  override fun itemUrl(slug: String): String = "$url/$slug"
  override fun libraryUrl(page: Int): String = "$url/directorio/$page"
  override fun schedulerUrl(): String = url
  override fun recentsUrl(): String = url

  override fun castType(type: String): AnimeType {
    return when (type) {
      "Anime", "Serie" -> AnimeType.TV
      "Pelicula" -> AnimeType.MOVIE
      "OVA" -> AnimeType.OVA
      "ONA" -> AnimeType.ONA
      "Especial" -> AnimeType.SPECIAL
      else -> AnimeType.UNKNOWN
    }
  }

  override fun castStatus(status: String): AnimeStatus {
    return when (status) {
      "En emision" -> AnimeStatus.ONGOING
      "Concluido" -> AnimeStatus.FINISHED
      "Por estrenar", "En espera" -> AnimeStatus.UPCOMING
      else -> AnimeStatus.UNKNOWN
    }
  }

  override fun parseItem(doc: String): Streaming {
    return htmlDocument(doc) {
      val slug = Regex("""(?<=jkanime.net/).*(?=/";)""").find(
        findFirst("#disqus_thread").siblings.first().html
      )!!.value
      val title = findFirst(".anime__details__text").findFirst("h3").text.trim()
      val synopsis = findFirst(".anime__details__text").findFirst("p").text.trim()
      val type =
        castType(findFirst(".anime__details__text").findAll("li").first { it.text.contains("Tipo") }.ownText.trim())
      val status = castStatus(findFirst(".anime__details__text").findAll("li").first { it.text.contains("Estado") }
        .findLast("span").text.trim())
      val picture = findFirst(".anime__details__pic.set-bg").attribute("data-setbg")
      val totalEpisodes = Regex("""\d+""").find(
        findFirst(".anime__details__text").findAll("li").first { it.text.contains("Episodios") }.text
      )?.value?.toIntOrNull() ?: 0
      var episodes = 0
      try {
        episodes =
          Regex("""\d+""").find(findFirst(".anime__pagination").findLast("a.numbers").text.split("-")[1])?.value?.toInt()
            ?: 0
      } catch (e: Exception) {
        println("[${source}] No episodes for: $slug")
      }
      return@htmlDocument Streaming(
        slug = slug,
        title = title,
        source = source,
        type = type,
        status = status,
        episodes = episodes,
        picture = picture
      )
    }
  }

  override fun parseLibraryPage(doc: String): List<Streaming> {
    return htmlDocument(doc) {
      val items = mutableListOf<Streaming>()
      findAll(".custom_item2").forEach {
        val slug = it.findFirst("a").attribute("href").split("/")[3]
        val title = it.findFirst("h5").text.trim()
        val type = castType(it.findSecond(".card-info p").text.trim())
        val status = castStatus(it.findFirst(".card-info p").text.trim())
        val picture = it.findFirst("img").attribute("src")
        val totalEpisodes = Regex("""\d+""").find(it.findFirst(".card-text.ep").ownText)?.value?.toIntOrNull() ?: 0
        println(totalEpisodes)
        items.add(
          Streaming(slug = slug, title = title, source = source, type = type, status = status, picture = picture)
        )
      }
      return@htmlDocument items
    }
  }

  override fun parseScheduler(doc: String): List<Scheduler> {
    return htmlDocument(doc) {
      val items = mutableListOf<Scheduler>()
      this.findAll(".listadoanime-home > div > a").forEach {
        val slug = it.attribute("href").split("/")[3]
        val episode = it.attribute("href").split("/")[4].toIntOrNull() ?: 0
        items.add(
          Scheduler(slug = slug, source = source, episode = episode)
        )
      }
      return@htmlDocument items
    }
  }

  override fun parseRecents(doc: String): List<Streaming> {
    return htmlDocument(doc) {
      val items = mutableListOf<Streaming>()
      this.findAll(".trending_div > .side-menu > li").forEach {
        val slug = it.findFirst("a").attribute("href").split("/")[3]
        val title = it.findFirst("a").text.trim()
        items.add(
          Streaming(slug = slug, title = title, source = source)
        )
      }
      return@htmlDocument items
    }
  }

}