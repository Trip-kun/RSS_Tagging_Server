package org.example

import io.javalin.http.Context
import org.example.data.Authentication
import org.example.data.User
import java.time.Instant
import javax.mail.Address
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress
import javax.xml.crypto.Data

fun register(context: Context) {
    var username: String = "";
    var password: String = "";
    var passwordConfirm: String= " ";
    var email: String = "";
    var emailConfirm: String= "";
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
    val userDao = DatabaseUtilities.getUserDao()
    userDao.queryForFieldValuesArgs(mapOf("username" to username)).firstOrNull()?.let {
        context.status(400)
        context.result("Username already exists.")
        return
    }
    try {
        val email = InternetAddress(emailConfirm)
        userDao.queryForFieldValuesArgs(mapOf("email" to email.address)).firstOrNull()?.let {
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
    val hashedPassword = SecureUtilities.hash(password)
    user.hashedPassword = hashedPassword.first
    user.salt = hashedPassword.second
    user.email = email;
    user.emailVerified = false;
    val emailToken = SecureUtilities.generateSalt(Config.getConfig().saltSize*2)
    val emailTokenHash = SecureUtilities.hash(emailToken)
    user.emailVerificationTokenHash = emailTokenHash.first
    user.emailVerificationSalt = emailTokenHash.second
    EmailUtils.sendEmail(email, "Email Verification", "Please click the following link to verify your email: "  + Config.getConfig().webUrl +"/verifyEmail?token=$emailToken&username=${user.username}")
    userDao.create(user)
    context.status(200)
    context.result("User created.")
}

fun verifyEmail(context: Context) {
    var token: String = "";
    var username: String = "";
    token = context.queryParam("token") ?: ""
    username = context.queryParam("username") ?: ""
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
    val userDao = DatabaseUtilities.getUserDao()

    userDao.queryForFieldValuesArgs(mapOf("username" to username)).firstOrNull()?.let {
        if (it.emailVerified) {
            context.status(200)
            context.result("Email already verified.")
            return
        }
        if (SecureUtilities.verifyHash(token, it.emailVerificationSalt, it.emailVerificationTokenHash)) {
            it.emailVerified = true
            it.emailVerificationSalt = ""
            it.emailVerificationTokenHash = ""
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
    var username: String = "";
    var password: String = "";
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
    val userDao = DatabaseUtilities.getUserDao()
    userDao.queryForFieldValuesArgs(mapOf("username" to username)).firstOrNull()?.let {
        if (SecureUtilities.verifyHash(password, it.salt, it.hashedPassword)) {
            context.status(200)
            val baseAuth = SecureUtilities.generateSalt(3*Config.getConfig().saltSize)
            val authenticationDao = DatabaseUtilities.getAuthenticationDao()
            val authentication = Authentication()
            authentication.user=it;
            val fullAuth = SecureUtilities.hash(baseAuth)
            authentication.hashedToken = fullAuth.first
            authentication.salt = fullAuth.second
            authentication.expiryUnix = Instant.now().epochSecond + Config.getConfig().authExpiry
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
    var authentication: String = "";
    var username: String = "";
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
    val authenticationDao = DatabaseUtilities.getAuthenticationDao()
    val userDao = DatabaseUtilities.getUserDao()
    userDao.queryForFieldValuesArgs(mapOf("username" to username)).firstOrNull()?.let {
        authenticationDao.queryForFieldValuesArgs(mapOf("user_id" to it.id)).forEach{
            if (SecureUtilities.verifyHash(authentication, it.salt, it.hashedToken)) {
                if (it.expiryUnix > Instant.now().epochSecond) {
                    return true;
                } else {
                    context.status(400)
                    context.result("Authentication token expired.")
                    authenticationDao.delete(it)
                    return false;
                }
            }
        }
    }
    context.status(400)
    context.result("Invalid authentication token.")
    return false
}