package com.lagradost

import android.util.Log
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.AppUtils.parseJson
import com.lagradost.cloudstream3.utils.AppUtils.toJson
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.M3u8Helper
import com.lagradost.cloudstream3.utils.Qualities
import com.lagradost.cloudstream3.LoadResponse.Companion.addTrailer
import org.jsoup.nodes.Element

/**
 * AnimeUnity Provider for CloudStream 3
 *
 * This provider integrates AnimeUnity.tv, an Italian anime streaming website,
 * into the CloudStream 3 app. It handles searching, browsing, and streaming anime content.
 *
 * Features:
 * - Main page with latest episodes, popular anime, and ongoing series
 * - Search functionality
 * - Episode extraction with quality options
 * - Subtitle support
 * - Italian language content
 */
class AnimeUnityProvider : MainAPI() {
    // Base configuration
    override var mainUrl = "https://www.animeunity.tv"
    override var name = "AnimeUnity"
    override val hasMainPage = true
    override var lang = "it"
    override val hasDownloadSupport = true
    override val supportedTypes = setOf(TvType.Anime)

    companion object {
        private const val TAG = "AnimeUnity"
    }

    // UI elements - Custom icons for the provider in CloudStream's interface
    override var iconId = R.drawable.ic_animeunity
    override var iconBackgroundId = R.drawable.ic_animeunity_background

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val document = app.get("$mainUrl/anime?page=$page").document
        val home = ArrayList<HomePageList>()

        // Latest episodes section
        val latestEpisodes = document.select("div.home-slider-content > div.anime-card-container")
            .mapNotNull { it.toSearchResult() }
        if (latestEpisodes.isNotEmpty()) home.add(HomePageList("Latest Episodes", latestEpisodes))
        
        // Most popular section
        val mostPopular = document.select("div.top-views > div.anime-card-container")
            .mapNotNull { it.toSearchResult() }
        if (mostPopular.isNotEmpty()) home.add(HomePageList("Most Popular", mostPopular))
        
        // Ongoing series
        val ongoing = document.select("div.ongoing-anime > div.anime-card-container")
            .mapNotNull { it.toSearchResult() }
        if (ongoing.isNotEmpty()) home.add(HomePageList("Ongoing Series", ongoing))

        return HomePageResponse(home)
    }

    private fun Element.toSearchResult(): SearchResponse? {
        val title = this.selectFirst("h3.card-title")?.text()?.trim() ?: return null
        val href = this.selectFirst("a")?.attr("href") ?: return null
        val posterUrl = this.selectFirst("img")?.attr("src")
        val id = href.split("/").lastOrNull() ?: return null
        
        return newAnimeSearchResponse(title, id) {
            this.posterUrl = posterUrl
            addDubStatus(dubExist = false, subExist = true)
        }
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val url = "$mainUrl/archivio?search=$query"
        val document = app.get(url).document

        return document.select("div.anime-card-container").mapNotNull { it.toSearchResult() }
    }

    override suspend fun load(url: String): LoadResponse {
        val animeId = url
        val document = app.get("$mainUrl/anime/$animeId").document
        
        val title = document.selectFirst("h1.anime-title")?.text()?.trim() ?: "Unknown Title"
        val posterUrl = document.selectFirst("div.locandina-container img")?.attr("src")
        val plot = document.selectFirst("div.description")?.text()?.trim()
        
        // Get other metadata
        val rating = document.selectFirst("div.vote > span")?.text()?.trim()?.toRatingInt()
        val tags = document.select("div.categories-container > span").map { it.text().trim() }
        val year = document.selectFirst("div.data-anime > span:contains(Anno)")?.text()?.substringAfter(":")?.trim()?.toIntOrNull()
        val status = getStatus(document.selectFirst("div.data-anime > span:contains(Stato)")?.text() ?: "")
        val trailerUrl = document.selectFirst("a.trailer-button")?.attr("href")
        
        // Get episodes
        val episodesElement = document.select("div.tab-pane:contains(Episodi) div.episodes-button")
        val episodes = mutableListOf<Episode>()
        
        episodesElement.forEach { element ->
            val episodeNumber = element.text().trim().toIntOrNull() ?: 0
            val episodeUrl = element.attr("data-id") ?: return@forEach

            episodes.add(
                Episode(
                    data = episodeUrl,
                    name = "Episode $episodeNumber",
                    episode = episodeNumber
                )
            )
        }

        return newAnimeLoadResponse(title, animeId, TvType.Anime) {
            posterUrl = posterUrl
            addEpisodes(DubStatus.Subbed, episodes)
            showStatus = status
            plot = plot
            this.year = year
            this.tags = tags
            this.rating = rating
            
            if (!trailerUrl.isNullOrEmpty()) {
                addTrailer(trailerUrl)
            }
        }
    }

    private fun getStatus(statusString: String): ShowStatus {
        return when {
            statusString.contains("In corso") -> ShowStatus.Ongoing
            statusString.contains("Finito") -> ShowStatus.Completed
            else -> ShowStatus.Unknown
        }
    }

    private fun String.toRatingInt(): Int? {
        val ratingString = this.replace(",", ".").trim()
        return try {
            (ratingString.toFloat() * 10).toInt()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val episodeId = data
        val episodeUrl = "$mainUrl/ajax/episode/info?id=$episodeId"
        
        try {
            val response = app.get(episodeUrl).text
            val jsonResponse = parseJson<AnimeUnityEpisodeResponse>(response)
            
            // Handle video links
            val videoUrl = jsonResponse.scws_id?.let { scwsId ->
                val server = "https://scws.work"
                "$server/master/$scwsId/master.m3u8"
            } ?: jsonResponse.link
            
            if (videoUrl.isNullOrEmpty()) {
                Log.e(TAG, "No video URL found for episode $episodeId")
                return false
            }
            
            if (videoUrl.contains(".m3u8")) {
                M3u8Helper.generateM3u8(
                    name,
                    videoUrl,
                    mainUrl
                ).forEach(callback)
            } else {
                // Direct link, usually mp4
                callback.invoke(
                    ExtractorLink(
                        name,
                        name,
                        videoUrl,
                        mainUrl,
                        Qualities.Unknown.value,
                        false
                    )
                )
            }
            
            // Handle subtitles if available
            jsonResponse.subtitles?.forEach { subtitle ->
                subtitleCallback.invoke(
                    SubtitleFile(
                        subtitle.label ?: "Italian",
                        subtitle.file ?: return@forEach
                    )
                )
            }
            
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error loading links: ${e.message}")
            return false
        }
    }
}

data class AnimeUnityEpisodeResponse(
    val link: String? = null,
    val scws_id: String? = null,
    val subtitles: List<Subtitle>? = null
)

data class Subtitle(
    val file: String? = null,
    val label: String? = null
)
