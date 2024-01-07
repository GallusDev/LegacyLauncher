import javafx.scene.paint.Color
import java.io.File

val serverName = "RuneLegacy"
val backgroundColor = Color.rgb(53, 53, 53)

// New
val clientLauncher = ClientLaunch()
val fileDownloader = FileDownloader()
val versionChecker = VersionChecker()

val clientDirectory = File(System.getProperty("user.home") + "/.rlegacy2/client")
val clientJar = File(clientDirectory, "client.jar")
val cacheDirectory = File(System.getProperty("user.home") + "/.rlegacy2/cache")
val outputDirectory = File(System.getProperty("user.home") + "/.rlegacy2")
val rtJar = File(clientDirectory, "rt.jar")

val cacheVersionUrl = "https://runelegacy.org/version-test/api/1.1/wf/cache_version"
val clientVersionUrl = "https://runelegacy.org/version-test/api/1.1/wf/client_version"

val cacheDownloadUrl = "https://www.runelegacy.org/testcache"
val clientDownloadUrl = "https://www.runelegacy.org/testclient"