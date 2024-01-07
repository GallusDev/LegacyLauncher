import javafx.scene.control.Alert
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TextArea
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import java.io.IOException
import java.nio.file.*

class VersionChecker {

    /**
     * Reads the settings from the "settings.properties" file located in the specified output directory.
     * This function attempts to parse and retrieve the cache and client versions from the properties file.
     *
     * @return A Pair containing the cache version and client version if successfully read from the file,
     *         or null if the file does not exist or the versions cannot be parsed.
     */
    fun readSettingsFile(): Pair<Int, Int>? {
        // Create a Properties object to hold the settings
        val properties = Properties()

        // Define the path to the settings file
        val settingsFile = File(outputDirectory, "settings.properties")

        // Check if the settings file exists
        if (settingsFile.exists()) {
            try {
                // Attempt to read the settings file using FileInputStream
                FileInputStream(settingsFile).use { input ->
                    properties.load(input)
                }

                // Retrieve the cache_version and client_version properties from the file
                val cacheVersionStr = properties.getProperty("cache_version")
                val clientVersionStr = properties.getProperty("client_version")

                // Check if both cache_version and client_version are not null
                if (clientVersionStr != null && cacheVersionStr != null) {
                    // Attempt to parse the versions as integers
                    val clientVersion = clientVersionStr.toIntOrNull()
                    val cacheVersion = cacheVersionStr.toIntOrNull()

                    // Check if parsing was successful for both versions
                    if (clientVersion != null && cacheVersion != null) {
                        // Return the versions as a Pair
                        return Pair(cacheVersion, clientVersion)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Return null if te file does not exist or the versions cannot be parsed
        return null
    }

    /**
     * Check game versions, update if necessary, and launch the client.
     *
     * @param progressIndicator The ProgressIndicator to show progress.
     * @param informationPanel The TextArea to display information to the user.
     */
    fun checkGameVersions(progressIndicator: ProgressIndicator, informationPanel: TextArea) {
        // Check if the client is open before checking for updates
        val fileInUse = isFileInUse(clientJar.toPath())
        if (fileInUse && clientJar.exists()) {
            displayClientOpenAlert()
            return
        }

        // Declare stored versions
        var storedCacheVersion: Int = 0
        var storedClientVersion: Int = 0

        // Declare live versions and initialize them to 0
        var liveCacheVersion = 0
        var liveClientVersion = 0

        // Retrieve live versions from external sources
        val liveCacheVersionStr = getCacheVersion(cacheVersionUrl)
        val liveClientVersionStr = getClientVersion(clientVersionUrl)

        // Convert the string versions to integers if possible
        liveCacheVersionStr?.toIntOrNull()?.let { liveCacheVersion = it }
        liveClientVersionStr?.toIntOrNull()?.let { liveClientVersion = it }

        // Read stored versions from settings file
        val storedVersions = readSettingsFile()

        if (storedVersions != null) {
            storedCacheVersion = storedVersions.first
            storedClientVersion = storedVersions.second
        } else {
            // No stored versions found, update files and create settings file
            informationPanel.appendText("Unable to verify file versions. Updating files...\n")
            fileDownloader.downloadClientAndCache(progressIndicator, informationPanel)
            createSettingsFile(liveCacheVersion, liveClientVersion)
        }

        // Check cache version
        if (liveCacheVersion != 0) {
            if (storedCacheVersion != liveCacheVersion) {
                informationPanel.appendText("Cache version update in progress. Please wait...\n")
                fileDownloader.downloadAndExtractCacheFiles(
                    cacheDownloadUrl,
                    cacheDirectory.path,
                    progressIndicator,
                    informationPanel
                )
            } else {
                informationPanel.appendText("Cache verified.\n")
            }
        } else {
            informationPanel.appendText("Unable to obtain cache version. Please contact an admin.")
        }

        // Check client version
        if (liveClientVersion != 0) {
            if (storedClientVersion != liveClientVersion) {
                informationPanel.appendText("Client version update in progress. Please wait...\n")
                fileDownloader.downloadAndExtractClientFiles(
                    clientDownloadUrl,
                    clientDirectory.path,
                    progressIndicator,
                    informationPanel
                )
            } else {
                informationPanel.appendText("Client verified.\n")
            }
        } else {
            informationPanel.appendText("Unable to obtain client version. Please contact an admin.")
        }

        updateSettingsFile(newCacheVersion = liveCacheVersion)
        updateSettingsFile(newClientVersion = liveClientVersion)
        informationPanel.appendText("All files are up to date.\n")
    }


    private fun getCacheVersion(url: String): String? {
        return retrieveVersion(url)
    }

    private fun getClientVersion(url: String): String? {
        return retrieveVersion(url)
    }

    /**
     * Retrieves a version string from a specified URL by making an HTTP GET request.
     *
     * @param url The URL from which to fetch the version.
     * @return The version string if the HTTP request is successful, or null otherwise.
     */
    private fun retrieveVersion(url: String): String? {
        // Create an instance of OkHttpClient for making HTTP requests
        val client = OkHttpClient()

        // Build an HTTP GET request with the provided URL
        val request = Request.Builder()
            .url(url)
            .build()

        return try {
            // Execute the HTTP request and get the response
            val response: Response = client.newCall(request).execute()

            // Check if the response status is successful (HTTP 200)
            if (response.isSuccessful) {
                // Retrieve the response body as a string (version)
                response.body?.string()
            } else {
                // Handle cases where the HTTP request fails and log the status code
                println("HTTP Request failed with code: ${response.code}")
                null
            }
        } catch (e: Exception) {
            // Handle exceptions that may occur during the HTTP request and log any errors
            println("Error: ${e.message}")
            null
        }
    }

    /**
     * Creates a settings file with cache and client version information.
     *
     * @param liveCacheVersion The live cache version to be stored in the settings file.
     * @param liveClientVersion The live client version to be stored in the settings file.
     */
    private fun createSettingsFile(liveCacheVersion: Int, liveClientVersion: Int) {
        val properties = Properties()
        val settingsFile = File(outputDirectory, "settings.properties")

        try {
            // Set the properties
            properties.setProperty("cache_version", liveCacheVersion.toString())
            properties.setProperty("client_version", liveClientVersion.toString())

            // Save the properties to the settings file
            FileOutputStream(settingsFile).use { output ->
                properties.store(output, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle any exceptions here
        }
    }

    /**
     * Updates the settings file with new cache and/or client version information if provided.
     *
     * @param newCacheVersion The new cache version to be updated in the settings file, or null to keep it unchanged.
     * @param newClientVersion The new client version to be updated in the settings file, or null to keep it unchanged.
     */
    private fun updateSettingsFile(newCacheVersion: Int? = null, newClientVersion: Int? = null) {
        val properties = Properties()
        val settingsFile = File(outputDirectory, "settings.properties")

        try {
            // Load existing properties if the file exists
            if (settingsFile.exists()) {
                FileInputStream(settingsFile).use { input ->
                    properties.load(input)
                }
            }

            // Update the properties with new cache version if provided
            newCacheVersion?.let {
                properties.setProperty("cache_version", it.toString())
            }

            // Update the properties with new client version if provided
            newClientVersion?. let {
                properties.setProperty("client_version", it.toString())
            }

            // Save the updated properties to the settings file
            FileOutputStream(settingsFile).use { output ->
                properties.store(output, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle any exceptions here
        }
    }

    private fun isFileInUse(file: Path): Boolean {
        try {
            // Attempt to move the file to a temporary location.
            // If it's in use, an exception will be thrown.
            Files.move(file, file.resolveSibling("tempfile"), StandardCopyOption.REPLACE_EXISTING)
            Files.move(file.resolveSibling("tempfile"), file, StandardCopyOption.REPLACE_EXISTING)
            return false // The file was not in use
        } catch (e: IOException) {
            return true // The file is in use
        }
    }

    private fun displayClientOpenAlert() {
        val alert = Alert(Alert.AlertType.WARNING)
        alert.title = "Warning!"
        alert.headerText = "Client open!"
        alert.contentText = "Please close your client before checking for updates."
        alert.showAndWait()
    }
}