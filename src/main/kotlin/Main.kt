package org.example

import io.javalin.Javalin
import io.javalin.community.ssl.SslPlugin
import io.javalin.community.ssl.TlsConfig
import io.javalin.config.JavalinConfig
import io.javalin.http.Context
import javax.xml.crypto.Data

fun main() {
    var config: Config = Config.getConfig()
    DatabaseUtilities.init()
    Javalin.create{javalinConfig -> createJavalin(javalinConfig)}
    .get("/") { ctx -> ctx.result(config.dbUser) }
    .get("/register") { ctx -> register(ctx) }
    .get("/login") { ctx -> login(ctx) }
    .get("/testAuthentication") { ctx -> testAuthentication(ctx) }
    .get("/verifyEmail") { ctx -> verifyEmail(ctx) }
    .start()
}
fun createJavalin(javalinConfig: JavalinConfig) {
    //* Temporarily disable SSL for development
    javalinConfig.registerPlugin(SslPlugin { conf ->
        //conf.pemFromPath("/path/to/cert.pem", "/path/to/key.pem")
        conf.insecure=true
        conf.secure=false
        conf.redirect=true
        conf.insecurePort=8081
        conf.securePort=443
        conf.tlsConfig= TlsConfig.MODERN
        // additional configuration options
    })
    //*/
}


//Do salting and javalin setup.
//Make basic authentication api.
//Make a basic authentication api.
//After that, work on the RSS feed stuff. Get an api plugin for that. 