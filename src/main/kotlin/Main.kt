package org.example

import io.javalin.Javalin
import io.javalin.community.ssl.SslPlugin
import io.javalin.config.JavalinConfig

fun main() {
    val config: Config = Config.getConfig()
    initDatabase()
    Javalin.create{javalinConfig -> createJavalin(javalinConfig)}
    .get("/") { ctx -> ctx.result(config.databaseSettings.databaseURL) }
    .get("/register") { ctx -> register(ctx) }
    .get("/login") { ctx -> login(ctx) }
    .get("/testAuthentication") { ctx -> testAuthentication(ctx) }
    .get("/verifyEmail") { ctx -> verifyEmail(ctx) }
    .get("/createChannel") { ctx -> createChannel(ctx) }
    .get("/getEntries") { ctx -> getEntries(ctx) }
    .get("/createOrUpdateTag") { ctx -> createOrUpdateTag(ctx) }
    .get("/getTags") { ctx -> getTags(ctx) }
    .get("/requestEmailAuthorization") { ctx -> requestEmailAuthorization(ctx) }
    .get("/getTaggedEntries") { ctx -> getTaggedEntriesForUser(ctx) }
    .get("/getChannels") { ctx -> getChannels(ctx) }
    .get("unsubscribe") { ctx -> unsubscribe(ctx) }
    .start()
}
fun createJavalin(javalinConfig: JavalinConfig) {
    val config: Config = Config.getConfig()
    javalinConfig.registerPlugin(SslPlugin { conf ->
        conf.insecure=!config.securitySettings.sslEnabled
        conf.secure=config.securitySettings.sslEnabled
        conf.redirect=true
        conf.insecurePort=config.securitySettings.insecurePort
        conf.securePort=config.securitySettings.sslPort
        conf.tlsConfig= config.securitySettings.tlsConfig
        if (config.securitySettings.sslEnabled) {
            conf.pemFromPath(config.securitySettings.sslCert, config.securitySettings.sslKey)
        }
    })
}
