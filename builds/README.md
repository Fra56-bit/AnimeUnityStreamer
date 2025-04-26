# Builds

Questa cartella contiene i file compilati del plugin AnimeUnity per CloudStream 3.

## AnimeUnity.zip

Questo file è il plugin compilato pronto per l'installazione in CloudStream 3. Viene generato automaticamente dal workflow GitHub Actions quando viene fatto un push sul branch main.

## Come funziona

1. Il file APK viene compilato con `./gradlew assembleDebug`
2. L'APK viene compresso in un file ZIP con nome `AnimeUnity.zip`
3. Il file ZIP viene caricato in questa cartella
4. Gli utenti possono installare il plugin tramite l'URL del repository in CloudStream 3

## Compilazione manuale

Se vuoi compilare manualmente il plugin:

1. Clona il repository
2. Configura correttamente l'Android SDK
3. Esegui `./gradlew assembleDebug`
4. Comprimi l'APK risultante (`app/build/outputs/apk/debug/app-debug.apk`) in un file chiamato `AnimeUnity.zip`
5. Sostituisci il file esistente in questa cartella