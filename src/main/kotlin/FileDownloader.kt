import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TextArea
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

class FileDownloader {

    fun downloadClientAndCache(progressIndicator: ProgressIndicator, informationPanel: TextArea) {
        // Update the informationPanel
        informationPanel.appendText("Downloading game files...\n")

        // Download and extract the cache files
        downloadAndExtractCacheFiles(cacheDownloadUrl, cacheDirectory.path, progressIndicator, informationPanel)
        // Download and extract the client files
        downloadAndExtractClientFiles(clientDownloadUrl, clientDirectory.path, progressIndicator, informationPanel)
    }

    @Throws(IOException::class)
    fun downloadAndExtractClientFiles(downloadUrl: String, destinationDir: String, progressIndicator: ProgressIndicator, informationPanel: TextArea) {
        // Update the informationPanel
        informationPanel.appendText("Obtaining Client files...\n")

        val zipFile = downloadFile(downloadUrl, progressIndicator)
        val extractionSuccessful = extractZipFile(zipFile, destinationDir)

        if (!extractionSuccessful) {
            Platform.runLater {
                val alert = Alert(Alert.AlertType.INFORMATION)
                alert.title = "Error Updating Files"
                alert.headerText = "This is the header text"
                alert.contentText = "This is the content text"
                alert.showAndWait()
            }
        }
    }

    @Throws(IOException::class)
    fun downloadAndExtractCacheFiles(downloadUrl: String, destinationDir: String, progressIndicator: ProgressIndicator, informationPanel: TextArea) {
        // Update the informationPanel
        informationPanel.appendText("Obtaining Cache files...\n")

        val zipFile = downloadFile(downloadUrl, progressIndicator)
        val extractionSuccessful = extractZipFile(zipFile, destinationDir)

        if (!extractionSuccessful) {
            Platform.runLater {
                val alert = Alert(Alert.AlertType.INFORMATION)
                alert.title = "Error Updating Files"
                alert.headerText = "This is the header text"
                alert.contentText = "This is the content text"
                alert.showAndWait()
            }
        }
    }

    /**
     * Downloads a file from the specified URL and displays the download progress using a ProgressIndicator.
     *
     * @param url The URL from which to download the file.
     * @param progressIndicator The ProgressIndicator to show the download progress.
     * @return The downloaded file.
     * @throws IOException If there is an error during the download process.
     */
    @Throws(IOException::class)
    private fun downloadFile(url: String, progressIndicator: ProgressIndicator) : File {
        // Set progress indicator visible to show that something is happening
        Platform.runLater{
            progressIndicator.isVisible = true
        }

        // Create an HttpClient instance
        val httpClient: CloseableHttpClient = HttpClients.createDefault()

        // Create an HTTP GET request
        val httpGet = HttpGet(url)

        // Execute the request and get the response
        val response = httpClient.execute(httpGet)

        // Check if the response status is OK (HTTP 200)
        if (response.statusLine.statusCode != 200) {
            // Handle the error, e.g., throw an exception or return null
            throw IOException("Failed to download file. HTTP status code: ${response.statusLine.statusCode}")
        }

        // Get the response entity
        val entity = response.entity

        // Exctract the file name from the URL
        val fileName = url.substring(url.lastIndexOf('/') + 1)

        // Ensure that the file name has the .zip exension
        val fileNameWithExtension = if (!fileName.endsWith(".zip")) {
            "$fileName.zip"
        } else {
            fileName
        }

        // Create the final output file in the specified directory
        val finalOutputFile = File(outputDirectory, fileNameWithExtension)

        // Get the input stream from th response entity
        val inputStream = entity.content

        // Create a FileOutputStream for writing the file
        val fileOutputStream = FileOutputStream(finalOutputFile)

        // Calculate the content length from the response headers
        val contentLength = entity.contentLength.toDouble()

        // Definte a buffer for reading data
        val buffer = ByteArray(1024 * 1024) // 1MB buffer size

        var bytesRead: Int
        var bytesTransferred = 0L

        try {
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                fileOutputStream.write(buffer, 0, bytesRead)
                bytesTransferred += bytesRead.toLong()
                Platform.runLater{
                    progressIndicator.progress = bytesTransferred / contentLength
                }
            }
        } finally {
            // Close the input stream, file output stream, and entity
            inputStream.close()
            fileOutputStream.close()
            EntityUtils.consume(entity)
        }

        // Close the HttpClient
        httpClient.close()

        Platform.runLater{
            progressIndicator.isVisible = false
            progressIndicator.progress = 0.0
        }

        return finalOutputFile
    }

    /**
     * Extracts the contents of a ZIP file to the specified destination directory.
     *
     * @param zipFile The ZIP file to extract.
     * @param destinationDir The destination directory where the ZIP file contents will be extracted.
     * @throws IOException If there is an error while extracting the ZIP file.
     */
    @Throws(IOException::class)
    private fun extractZipFile(zipFile: File, destinationDir: String): Boolean {
        // Get the path to the destination directory
        val destDirPath = Paths.get(destinationDir)

        // Create a ZIP input stream from the provided ZIP file
        val zipInputStream = ZipInputStream(zipFile.inputStream())

        // Initialize the ZIP entry
        var entry: ZipEntry? = zipInputStream.nextEntry

        // Iterate through each ZIP entry and extract its contents
        while (entry != null) {
            // Resolve the path for the current ZIP entry within the destination directory
            val entryPath = destDirPath.resolve(entry.name)

            // Check if the ZIP entry represents a file
            if (!entry.isDirectory) {
                try {
                    Files.createDirectories(entryPath.parent)
                    Files.copy(zipInputStream, entryPath, StandardCopyOption.REPLACE_EXISTING)
                } catch (e: FileSystemException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                Files.createDirectories(entryPath)
            }

            // Move to the next ZIP entry
            entry = zipInputStream.nextEntry
        }

        // Return true to indicate successful extraction
        return true
    }
}