import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.File
import java.security.KeyStore

fun main() {
    val keystoreFile = File("keystore.jks")
    val keystorePassword = "123456789"
    val privateKeyPassword = "123456789"

    // Load the keystore
    val keyStore = KeyStore.getInstance("JKS").apply {
        load(keystoreFile.inputStream(), keystorePassword.toCharArray())
    }

    // Define the SSL connector configuration
    val environment = applicationEngineEnvironment {
        sslConnector(
            keyStore = keyStore,
            keyAlias = "mycert",
            keyStorePassword = { keystorePassword.toCharArray() },
            privateKeyPassword = { privateKeyPassword.toCharArray() }
        ) {
            port = 8443
            keyStorePath = keystoreFile.absoluteFile
        }

        module(Application::module)
    }

    embeddedServer(Netty, environment).start(wait = true)
}

fun Application.module() {
    install(CallLogging)
    routing {
        get("/") {
            call.respondText("<html><body><h1>Hello, world!</h1></body></html>", ContentType.Text.Html)
        }
    }
}
