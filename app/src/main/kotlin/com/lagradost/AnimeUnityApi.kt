package com.lagradost

import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.utils.AppUtils.parseJson
import okhttp3.Headers
import org.jsoup.Jsoup

object AnimeUnityApi {
    private const val mainUrl = "https://www.animeunity.tv"
    private const val apiUrl = "$mainUrl/api"
    
    private val headers = Headers.Builder()
        .add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
        .add("Referer", mainUrl)
        .build()
    
    /**
     * Search for anime by name
     */
    suspend fun searchAnime(query: String): List<SearchResult> {
        val url = "$apiUrl/anime/search?q=$query"
        val response = app.get(url, headers = headers).text
        return parseJson<List<SearchResult>>(response)
    }
    
    /**
     * Get anime details by ID
     */
    suspend fun getAnimeDetails(id: Int): AnimeDetails? {
        val url = "$apiUrl/anime/$id"
        val response = app.get(url, headers = headers).text
        return parseJson<AnimeDetails>(response)
    }
    
    /**
     * Get episode information by ID
     */
    suspend fun getEpisodeInfo(id: Int): EpisodeInfo? {
        val url = "$apiUrl/episode/$id"
        val response = app.get(url, headers = headers).text
        return parseJson<EpisodeInfo>(response)
    }
    
    /**
     * Get latest anime episodes
     */
    suspend fun getLatestEpisodes(page: Int = 1): List<EpisodeUpdate> {
        val url = "$apiUrl/latest?page=$page"
        val response = app.get(url, headers = headers).text
        return parseJson<List<EpisodeUpdate>>(response)
    }
    
    data class SearchResult(
        val id: Int,
        val title: String,
        val slug: String,
        val status: String,
        val image_url: String,
        val type: String
    )
    
    data class AnimeDetails(
        val id: Int,
        val title: String,
        val alt_title: String?,
        val slug: String,
        val status: String,
        val image_url: String,
        val plot: String?,
        val year: Int?,
        val genres: List<String>?,
        val episodes: List<Episode>?,
        val trailer_url: String?
    )
    
    data class Episode(
        val id: Int,
        val number: Int,
        val title: String?,
        val created_at: String
    )
    
    data class EpisodeInfo(
        val id: Int,
        val number: Int,
        val anime_id: Int,
        val video_url: String?,
        val scws_id: String?,
        val subtitles: List<Subtitle>?
    )
    
    data class Subtitle(
        val lang: String,
        val file: String
    )
    
    data class EpisodeUpdate(
        val id: Int,
        val anime_id: Int,
        val number: Int,
        val anime_title: String,
        val anime_image: String
    )
}
