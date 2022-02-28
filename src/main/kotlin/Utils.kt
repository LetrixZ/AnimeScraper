import it.skrape.core.htmlDocument
import it.skrape.fetcher.AsyncFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.pathString

object Utils {

  private val httpClient: HttpClient = HttpClient.newBuilder().build()
  val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
  }

  fun String.toKebabCase(): String {
    val s = this.lowercase()
    val match = Regex("""[A-z0-9]+""").findAll(s).map { it.value }.joinToString(" ")
    return match.replace(" ", "-")
  }

  val resourcesPath: Path = Paths.get(
    Paths.get("").toAbsolutePath().pathString, "/resources"
  )

  suspend fun downloadHTML(URL: String): String = skrape(AsyncFetcher) {
    request {
      url = URL
    }
    response {
      htmlDocument {
        return@htmlDocument this.html
      }
    }
  }

  fun post(data: String, URL: String): HttpResponse<String> {
    val request = HttpRequest.newBuilder().uri(URI.create("http://127.0.0.1:4000/$URL"))
      .POST(HttpRequest.BodyPublishers.ofString(data)).header("Content-Type", "application/json").build()
    return httpClient.send(request, HttpResponse.BodyHandlers.ofString())
  }

  fun put(data: String, URL: String): HttpResponse<String> {
    val request = HttpRequest.newBuilder().uri(URI.create("http://127.0.0.1:4000/$URL"))
      .PUT(HttpRequest.BodyPublishers.ofString(data)).header("Content-Type", "application/json").build()
    return httpClient.send(request, HttpResponse.BodyHandlers.ofString())
  }

  fun get(URL: String): HttpResponse<String> {
    val request = HttpRequest.newBuilder().uri(URI.create("http://127.0.0.1:4000/$URL")).GET().build()
    return httpClient.send(request, HttpResponse.BodyHandlers.ofString())
  }
}