# AnimeUnity Plugin for CloudStream 3

A CloudStream 3 plugin that integrates AnimeUnity's (animeunity.to) anime library with powerful streaming capabilities.

## Features

- 🔍 **Search functionality**: Easily find and watch your favorite anime
- 🎬 **Video quality options**: Select your preferred streaming quality
- 🗣️ **Subtitle support**: Complete integration with subtitle information
- 🔄 **Dubbing support**: Watch anime with Italian dubbing when available
- 📱 **Responsive design**: Fully integrated with CloudStream 3's user-friendly interface

## Installation

1. Open CloudStream 3
2. Go to Settings > Extensions
3. Click on "Repositories" and add the repository URL:
   ```
   https://raw.githubusercontent.com/YOUR_USERNAME/REPOSITORY_NAME/main/
   ```
   (Replace YOUR_USERNAME and REPOSITORY_NAME with your actual GitHub username and repository name)
4. Install the AnimeUnity plugin from the available extensions
5. Restart CloudStream 3

## Development

This plugin is developed using Kotlin and follows the CloudStream 3 plugin architecture. To contribute or modify:

1. Clone the repository
2. Make changes to the Kotlin files in the `app/src/main` directory
3. Build using Gradle: `./gradlew assembleDebug`
4. The output APK will be in `app/build/outputs/apk/debug/`

## Structure

- `AnimeUnityPlugin.kt`: Main plugin entry point
- `AnimeUnityProvider.kt`: Implementation of the anime provider
- `AnimeUnityApi.kt`: API client for AnimeUnity
- `ExtractorApi.kt`: Video extraction helpers

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Disclaimer

This plugin is not affiliated with or endorsed by AnimeUnity.to. It is an independent project created for educational purposes and personal use within the CloudStream 3 ecosystem.