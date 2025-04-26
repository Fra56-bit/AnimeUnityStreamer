package com.lagradost

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

/**
 * AnimeUnity Plugin for CloudStream 3
 * 
 * This is the main plugin class that CloudStream 3 loads.
 * It registers the AnimeUnityProvider as a source for anime content.
 *
 * The @CloudstreamPlugin annotation is required for CloudStream to detect this plugin.
 */
@CloudstreamPlugin
class AnimeUnityPlugin: Plugin() {
    override fun load(context: Context) {
        // Register the AnimeUnityProvider to the main app
        // This makes the provider visible in CloudStream's sources list
        registerMainAPI(AnimeUnityProvider())
    }
}
