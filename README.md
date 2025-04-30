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
   https://raw.githubusercontent.com/your-github-username/AnimeUnityStreamer/main/repo.json
   ```
4. Install the AnimeUnity plugin from the available extensions
5. Restart CloudStream 3

## Development

This plugin is developed using Kotlin and follows the CloudStream 3 plugin architecture. To contribute or modify:

1. Clone the repository
2. Make changes to the Kotlin files in the `app/src/main` directory
3. Build using Gradle: `./gradlew assembleDebug`
4. The output APK will be in `app/build/outputs/apk/debug/`
5. Compress the APK into a ZIP file named `AnimeUnity.zip`
6. Place the ZIP file in the `builds` directory

### Automatic Building

This repository uses GitHub Actions to automatically build the plugin when changes are pushed to the main branch. The workflow:

1. Compiles the plugin using Gradle
2. Creates the `AnimeUnity.zip` file
3. Updates the file in the `builds` directory
4. Makes the compiled plugin available for download

## Structure

- `AnimeUnityPlugin.kt`: Main plugin entry point
- `AnimeUnityProvider.kt`: Implementation of the anime provider
- `AnimeUnityApi.kt`: API client for AnimeUnity
- `ExtractorApi.kt`: Video extraction helpers

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Disclaimer

This plugin is not affiliated with or endorsed by AnimeUnity.to. It is an independent project created for educational purposes and personal use within the CloudStream 3 ecosystem."# AnimeUnityStreamer" 
