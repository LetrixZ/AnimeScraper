package sites

import Utils
import Utils.json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.Scheduler
import models.Streaming
import models.enums.AnimeStatus
import models.enums.AnimeType
import models.enums.StreamingType
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.pathString

abstract class BaseSite {
  abstract val source: StreamingType
  abstract val url: String

  abstract fun itemUrl(slug: String): String
  abstract fun libraryUrl(page: Int): String
  abstract fun schedulerUrl(): String
  abstract fun recentsUrl(): String
  abstract fun castType(type: String): AnimeType
  abstract fun castStatus(status: String): AnimeStatus

  private val path: Path by lazy {
    Paths.get(
      Paths.get("").toAbsolutePath().pathString, "/resources/${source.name.lowercase()}"
    )
  }

  private val itemsPath: Path by lazy { Paths.get(path.pathString, "items") }
  private val libraryPath: Path by lazy { Paths.get(path.pathString, "library") }

  private fun getItemsDoc(): List<String> {
    return Files.walk(itemsPath).filter { Files.isRegularFile(it) }.filter { it.extension == "html" }
      .map { Files.readString(it) }.toList()
  }

  private fun getItemDoc(slug: String): String? {
    return Files.walk(itemsPath).filter { Files.isRegularFile(it) }.filter { it.extension == "html" }
      .filter { it.nameWithoutExtension == slug }.map { Files.readString(it) }.toList().firstOrNull()
  }

  private suspend fun downloadItem(slug: String, local: Boolean): String {
    println("[${source}] Downloading: ${slug}")
    val doc: String
    if (local) {
      val localDoc = getItemDoc(slug)
      if (localDoc != null) {
        doc = localDoc
        return doc
      }
    }
    doc = Utils.downloadHTML(itemUrl(slug))
    File("${itemsPath.pathString}/$slug.html").writeText(doc)
    return doc
  }

  abstract fun parseItem(doc: String): Streaming

  suspend fun getItem(slug: String, local: Boolean = false): Streaming {
    val doc = downloadItem(slug, local)
    return parseItem(doc)
  }

  fun getItems(local: Boolean = false): List<Streaming> {
    if (local && Paths.get(path.pathString, "items.json").exists())
      return Json.decodeFromString(File("${path.pathString}/items.json").readText())
    val docs = getItemsDoc()
    val items = docs.map {
      parseItem(doc = it)
    }
    File("${path.pathString}/items.json").writeText(json.encodeToString(items))
    return items
  }

  private suspend fun downloadLibraryPage(page: Int, local: Boolean): String {
    val doc: String
    if (local) {
      doc = File("${libraryPath.pathString}/$page.html").readText()
    } else {
      doc = Utils.downloadHTML(libraryUrl(page))
      File("${libraryPath.pathString}/$page.html").writeText(doc)
    }
    return doc
  }

  abstract fun parseLibraryPage(doc: String): List<Streaming>

  suspend fun getLibraryPage(page: Int, local: Boolean = false): List<Streaming> {
    val doc = downloadLibraryPage(page, local)
    return parseLibraryPage(doc)
  }

  private suspend fun downloadScheduler(): String {
    return Utils.downloadHTML(schedulerUrl())
  }

  abstract fun parseScheduler(doc: String): List<Scheduler>

  suspend fun getScheduler(): List<Scheduler> {
    val doc = downloadScheduler()
    return parseScheduler(doc)
  }

  private suspend fun downloadRecents(): String {
    return Utils.downloadHTML(recentsUrl())
  }

  abstract fun parseRecents(doc: String): List<Streaming>
  suspend fun getRecents(): List<Streaming> {
    val doc = downloadRecents()
    return parseRecents(doc)
  }
}