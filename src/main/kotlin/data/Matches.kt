package data

import Utils
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import models.MatchItem
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.pathString

object Matches {

  fun getMatches(): List<MatchItem> {
    return Files.walk(Paths.get(Utils.resourcesPath.pathString, "matches"))
      .filter { Files.isRegularFile(it) }
      .map { return@map Json.decodeFromString<List<MatchItem>>(Files.readString(it)) }
      .toList()
      .flatten()
  }

}