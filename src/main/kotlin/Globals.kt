import javafx.scene.paint.Color
import java.io.File

/**
 * This file contains values that are used throughout the program
 * If any value needs updated, it should be updated from within ths file
 * only.
 */

const val serverName = "RuneLegacy"

// Important Links
const val cacheVersionUrl = "https://runelegacy.org/version-test/api/1.1/wf/cache_version"
const val clientVersionUrl = "https://runelegacy.org/version-test/api/1.1/wf/client_version"
const val cacheDownloadUrl = "https://www.runelegacy.org/testcache"
const val clientDownloadUrl = "https://www.runelegacy.org/testclient"

// Directories and Files
val clientDirectory = File(System.getProperty("user.home") + "/.rlegacy2/client")
val clientJar = File(clientDirectory, "client.jar")
val cacheDirectory = File(System.getProperty("user.home") + "/.rlegacy2/cache")
val outputDirectory = File(System.getProperty("user.home") + "/.rlegacy2")
val rtJar = File(clientDirectory, "rt.jar")

// Class instances
val clientLauncher = ClientLaunch()
val fileDownloader = FileDownloader()
val versionChecker = VersionChecker()

// Colors and Styles
val backgroundColor: Color = Color.rgb(53, 53, 53)

// Text colors
val textColorRed = "-fx-text-fill: #C71414;"
val textColorOrange = "-fx-text-fill: #D49C1C;"
val textColorGreen = "-fx-text-fill: #14A819;"

// Styling for the buttons with hover effect
val launchButtonStyle = """
    -fx-min-width: 150px;
    -fx-min-height: 50px;
    -fx-background-color: #14A819;
    -fx-font-weight: bold;
    -fx-text-fill: white;
"""
val updateButtonStyle = """
    -fx-min-width: 150px;
    -fx-min-height: 50px;
    -fx-background-color: #D49C1C;
    -fx-font-weight: bold;
    -fx-text-fill: white;
"""

val launchButtonHoverStyle = "-fx-background-color: #0F8A11;" // Slightly lighter color on hover
val updateButtonHoverStyle = "-fx-background-color: #BB8415;" // Slightly lighter color on hover

