package org.example

import io.javalin.http.Context
import org.example.data.Authentication
import org.example.data.User
import java.time.Instant
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress

fun register(context: Context) {
    var username: String = ""
    var password: String = ""
    var passwordConfirm: String= " "
    var email: String = ""
    var emailConfirm: String= ""
    context.headerMap().forEach { (key, value) ->
        when (key) {
            "REGISTER_username" -> username = value
            "REGISTER_password" -> password = value
            "REGISTER_passwordConfirm" -> passwordConfirm = value
            "REGISTER_email" -> email = value
            "REGISTER_emailConfirm" -> emailConfirm = value
        }
    }
    if (password != passwordConfirm) {
        context.status(400)
        context.result("Passwords do not match.")
        return
    }
    if (email != emailConfirm) {
        context.status(400)
        context.result("Emails do not match.")
        return
    }
    if (username == "" || password == "" || email == "") {
        context.status(400)
        context.result("All fields must be filled out.")
        return
    }
    if (username.length < 6) {
        context.status(400)
        context.result("Username must be at least 6 characters long.")
        return
    }
    if (password.length < 8) {
        context.status(400)
        context.result("Password must be at least 8 characters long.")
        return
    }
    if (email.length < 6) {
        context.status(400)
        context.result("Email must be at least 6 characters long.")
        return
    }
    //Need to "simplify" the email to prevent duplicate accounts.
    userDao.queryForFieldValuesArgs(mapOf("username" to username))?.firstOrNull()?.let {
        context.status(400)
        context.result("Username already exists.")
        return
    }
    try {
        val emailIA = InternetAddress(emailConfirm)
        userDao.queryForFieldValuesArgs(mapOf("email" to emailIA.address))?.firstOrNull()?.let {
            context.status(400)
            context.result("Email already exists.")
            return
        }
    } catch (ex: AddressException) {
        context.status(400)
        context.result("Invalid email.")
        return
    }

    val user = User()
    user.username = username
    val saltHash: SaltHash = hashPassword(password)
    user.hashedPassword = saltHash.hash
    user.passwordSalt = saltHash.salt
    user.email = email
    user.emailVerified = false
    sendEmailVerification(context, user)
    userDao.create(user)
    context.status(200)
    context.result("User created.")
}

fun verifyEmail(context: Context) {
    val token: String = context.queryParam("token") ?: ""
    val username: String = context.queryParam("username") ?: ""
    if (token == "") {
        context.status(400)
        context.result("Token not provided.")
        return
    }
    if (username == "") {
        context.status(400)
        context.result("Username not provided.")
        return
    }

    userDao.queryForFieldValuesArgs(mapOf("username" to username)).firstOrNull()?.let {
        if (it.emailVerified) {
            context.status(200)
            context.result("Email already verified.")
            return
        }
        if (verifyPassword(token, SaltHash(it.emailTokenSalt.toString(), it.hashedEmailToken.toString())) && (Instant.now().epochSecond<it.emailTokenExpiryUnix)) {
            it.emailVerified = true
            it.emailTokenSalt = null
            it.hashedEmailToken = null
            userDao.update(it)
            context.status(200)
            context.result("Email verified.")
            return
        }
    }
    context.status(400)
    context.result("Invalid token.")
}

fun login(context: Context) {
    var username: String = ""
    var password: String = ""
    context.headerMap().forEach { (key, value) ->
        when (key) {
            "LOGIN_username" -> username = value
            "LOGIN_password" -> password = value
        }
    }
    if (username == "" || password == "") {
        context.status(400)
        context.result("All fields must be filled out.")
        return
    }
    userDao.queryForFieldValuesArgs(mapOf("username" to username)).firstOrNull()?.let {
        if (verifyPassword(password, SaltHash(it.passwordSalt, it.hashedPassword))) {
            context.status(200)
            val baseAuth = generateSalt(Config.getConfig().securitySettings.saltSize*3)
            val authentication = Authentication()
            authentication.user=it
            val fullAuth = hashPassword(baseAuth)
            authentication.hashedToken = fullAuth.hash
            authentication.tokenSalt = fullAuth.salt
            authentication.expiryUnix = Instant.now().epochSecond + Config.getConfig().securitySettings.authorizationExpiry
            authenticationDao.create(authentication)
            context.header("Authentication", baseAuth)
            context.result("Login successful.")
            return
        }
    }
    context.status(400)
    context.result("Invalid username or password.")
}

fun testAuthentication(context: Context): Boolean {
    var authentication: String = ""
    var username: String = ""
    context.headerMap().forEach { (key, value) ->
        when (key) {
            "Authentication" -> authentication = value
            "LOGIN_username" -> username = value
        }
    }
    if (authentication == "") {
        context.status(400)
        context.result("Authentication token not provided.")
        return false
    }
    if (username == "") {
        context.status(400)
        context.result("Username not provided.")
        return false
    }
    userDao.queryForFieldValuesArgs(mapOf("username" to username)).firstOrNull()?.let { user ->
        authenticationDao.queryForFieldValuesArgs(mapOf("user_id" to user.id)).forEach{
            if (verifyPassword(authentication, SaltHash(it.tokenSalt, it.hashedToken))) {
                if (it.expiryUnix > Instant.now().epochSecond) {
                    return true
                } else {
                    context.status(400)
                    context.result("Authentication token expired.")
                    authenticationDao.delete(it)
                    return false
                }
            }
        }
    }
    context.status(400)
    context.result("Invalid authentication token.")
    return false
}

fun requestEmailAuthorization(context: Context) {
    var username: String = ""
    context.headerMap().forEach { (key, value) ->
        when (key) {
            "LOGIN_username" -> username = value
        }
    }
    if (username == "") {
        context.status(400)
        context.result("Username not provided.")
        return
    }
    userDao.queryForFieldValuesArgs(mapOf("username" to username)).firstOrNull()?.let {
        if (it.emailVerified) {
            context.status(200)
            context.result("Email already verified.")
            return
        }
        sendEmailVerification(context, it)
        context.status(200)
        context.result("Email sent.")
        return
    }
    context.status(400)
    context.result("Invalid username.")
}