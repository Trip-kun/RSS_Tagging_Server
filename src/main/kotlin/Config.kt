package org.example

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.javalin.community.ssl.TlsConfig
import okio.IOException
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path

val GsonBuilder = GsonBuilder()
val gson: Gson = GsonBuilder.create()
class Config {
    inner class ThreadSettings{
        val threadLimit: Int = 50
        val threadSleep: Int = 100
    }
    inner class DatabaseSettings {
        val databaseURL: String = ""
        val databaseUser: String = ""
        val databasePassword: String = ""
        val databasePort: Int = 3306
        val databaseName: String = ""
        val databaseMaxRetries: Int = 3
        val databaseRetryDelay: Int = 1000
        val databaseMaxConnections: Int = 10
        val databaseMaxResponses: Int = 10
    }
    inner class EmailSettings {
        val emailHost: String = ""
        val emailPort: String = ""
        val emailUsername: String = ""
        val emailPassword: String = ""
    }
    inner class SecuritySettings {
        val saltSize: Int = 16
        val hashCost: Int = 12
        val authorizationExpiry: Long = 60 * 60 * 24 * 7
        val sslCert: String = ""
        val sslKey: String = ""
        val sslEnabled: Boolean = false
        val sslPort: Int = 443
        val insecurePort: Int = 8081
        val tlsConfig: TlsConfig = TlsConfig.MODERN
        val emailAuthorizationEnabled: Boolean = false
    }
    var threadSettings: ThreadSettings = ThreadSettings()
    var databaseSettings: DatabaseSettings = DatabaseSettings()
    var emailSettings: EmailSettings = EmailSettings()
    var securitySettings: SecuritySettings = SecuritySettings()
    val version: Int = 1
    val webUrl: String = ""
    val maxEntries = 100

    companion object {
        @JvmStatic
        @Transient
        private var config: Config? = null
        @JvmStatic
        fun getConfig(): Config {
            if (config!=null) {
                return config as Config
            }
            val configPath: String = System.getProperty("user.dir") + "/config.json"
            val path: Path = Path.of(configPath)
            val exists: Boolean = Files.exists(path)
            try {
                if (!exists) {
                    FileOutputStream(configPath, true).close()
                }
                val fileRW: FileRW =  FileRW(path)
                val content: String = fileRW.read()
                if (content.isEmpty() || content == "null") {
                    config = Config()
                    fileRW.write(gson.toJson(config))
                } else {
                    config = gson.fromJson(content, Config::class.java)
                }
            } catch (e: IOException) {
                throw RuntimeException("Failed to read config file: ", e)
            }
            return config as Config
        }
    }
}