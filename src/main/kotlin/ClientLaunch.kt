import java.io.File
import java.io.IOException

class ClientLaunch {

    fun launchClient() : Boolean{
        if (!clientJar.exists()) {
            println("client.jar not found at ${clientJar.absolutePath}")
            return false
        }

        if (!rtJar.exists()) {
            println("rt.jar not found at ${rtJar.absolutePath}")
            return false
        }

        return try {
            // Construct the classpath to include both client.jar and rt.jar
            val classpath = "${clientJar.absolutePath}${File.pathSeparator}${rtJar.absolutePath}"

            // Launch the client using the constructed classpath
            val builder = ProcessBuilder("java", "-cp", classpath, "client.Client")
            builder.directory(clientDirectory)
            val process = builder.start()

            // Check if the process started successfully
            process.isAlive
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

}