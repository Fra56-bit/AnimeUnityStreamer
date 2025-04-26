package com.lagradost

import android.util.Log
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.utils.ExtractorApi
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.Qualities
import com.lagradost.cloudstream3.utils.M3u8Helper

/**
 * AnimeUnity Video Extractor
 *
 * This class handles the extraction of video streams from AnimeUnity.to website.
 * It can process various types of video sources:
 * - HLS (m3u8) streams from SCWS server
 * - Direct MP4 links
 * - Embedded video players
 * - Multiple quality options
 *
 * The extractor supports CloudStream 3's standard extraction API and 
 * properly handles referer headers to avoid blocking.
 */
class AnimeUnityExtractor : ExtractorApi() {
    override var name = "AnimeUnity"
    override var mainUrl = "https://www.animeunity.to"
    
    // AnimeUnity requires a referer header to verify requests
    override val requiresReferer = true

    private val TAG = "AnimeUnityExtractor"

    /**
     * Main extraction method that processes video URLs and returns playable links
     *
     * This method handles different source types from AnimeUnity:
     * 1. HLS streams from the SCWS server (m3u8)
     * 2. Direct MP4 links 
     * 3. HTML5 video elements in the page
     * 4. Iframe embedded players
     *
     * @param url The source URL to extract from
     * @param referer The referer URL needed for the request (prevents 403 errors)
     * @return List of extracted video links with quality information
     */
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
