import javafx.scene.paint.Color
import java.io.File

const val serverName = "RuneLegacy"
val backgroundColor: Color = Color.rgb(53, 53, 53)

// New
val clientLauncher = ClientLaunch()
val fileDownloader = FileDownloader()
val versionChecker = VersionChecker()

val clientDirectory = File(System.getProperty("user.home") + "/.rlegacy2/client")
val clientJar = File(clientDirectory, "client.jar")
val cacheDirectory = File(System.getProperty("user.home") + "/.rlegacy2/cache")
val outputDirectory = File(System.getProperty("user.home") + "/.rlegacy2")
val rtJar = File(clientDirectory, "rt.jar")

const val cacheVersionUrl = "https://runelegacy.org/version-test/api/1.1/wf/cache_version"
const val clientVersionUrl = "https://runelegacy.org/version-test/api/1.1/wf/client_version"

const val cacheDownloadUrl = "https://www.runelegacy.org/testcache"
const val clientDownloadUrl = "https://www.runelegacy.org/testclient"