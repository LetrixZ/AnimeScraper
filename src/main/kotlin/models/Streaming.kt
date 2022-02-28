package models

import models.enums.AnimeStatus
import models.enums.AnimeType
import models.enums.StreamingType

@kotlinx.serialization.Serializable
data class Streaming(
    val slug: String,
    val source: StreamingType,
    val title: String,
    val type: AnimeType = AnimeType.UNKNOWN,
    val status: AnimeStatus = AnimeStatus.UNKNOWN,
    val episodes: Int = 0,
    val picture: String? = null
)
