package com.lagradost

import android.util.Log
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.utils.ExtractorApi
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.Qualities
import com.lagradost.cloudstream3.utils.M3u8Helper

class AnimeUnityExtractor : ExtractorApi() {
    override var name = "AnimeUnity"
    override var mainUrl = "https://www.animeunity.tv"
    
    override val requiresReferer = true

    private val TAG = "AnimeUnityExtractor"

    override suspend fun getUrl(url: String, referer: String?): List<ExtractorLink> {
        val extractedLinks = mutableListOf<ExtractorLink>()
        
        try {
            // Check if it's an SCWS (AnimeUnity's video server) URL
            if (url.contains("scws.work")) {
                M3u8Helper.generateM3u8(
                    name,
                    url,
                    referer ?: mainUrl
                ).forEach { extractedLinks.add(it) }
                return extractedLinks
            }
            
            // If it's a direct MP4 link
            if (url.endsWith(".mp4")) {
                extractedLinks.add(
                    ExtractorLink(
                        name,
                        name,
                        url,
                        referer ?: mainUrl,
                        Qualities.Unknown.value,
                        false
                    )
                )
                return extractedLinks
            }
            
            // If it's another provider embedded in AnimeUnity
            val response = app.get(url, referer = referer)
            val document = response.document
            
            // Try to find a video source in the page
            document.select("video source").forEach { source ->
                val srcUrl = source.attr("src")
                if (srcUrl.isNotEmpty()) {
                    val quality = source.attr("data-quality").toIntOrNull() ?: Qualities.Unknown.value
                    
                    extractedLinks.add(
                        ExtractorLink(
                            name,
                            name,
                            srcUrl,
                            referer ?: mainUrl,
                            quality,
                            srcUrl.contains(".m3u8")
                        )
                    )
                }
            }
            
            // Look for iframe sources
            document.select("iframe").forEach { iframe ->
                val iframeSrc = iframe.attr("src")
                if (iframeSrc.isNotEmpty()) {
                    // Handle embedded players here
                    // For this example, we'll just use a placeholder
                    Log.i(TAG, "Found iframe source: $iframeSrc")
                    // Would need specific extractors for each possible embedded source
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting links: ${e.message}")
        }
        
        return extractedLinks
    }
}
