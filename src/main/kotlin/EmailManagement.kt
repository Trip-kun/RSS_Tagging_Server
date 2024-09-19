package org.example

import io.javalin.http.Context
import org.example.data.User
import java.time.Instant
import java.util.*
import javax.mail.Authenticator
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
private val config: Config = Config.getConfig()
fun sendEmail(recipient: String, subject: String, body: String) {
    val config: Config = Config.getConfig()
    val prop: Properties = System.getProperties()
    prop.setProperty("mail.smtp.starttls.enable", "true")
    prop.setProperty("mail.smtp.host", config.emailSettings.emailHost)
    prop.setProperty("mail.smtp.user", config.emailSettings.emailUsername)
    prop.setProperty("mail.smtp.password", config.emailSettings.emailPassword)
    prop.setProperty("mail.smtp.port", config.emailSettings.emailPort)
    prop.setProperty("mail.smtp.auth", "true")

    val session: Session = Session.getInstance(prop, object : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication(config.emailSettings.emailUsername, config.emailSettings.emailPassword)
        }
    })
    val message: MimeMessage = MimeMessage(Session.getDefaultInstance(System.getProperties()))
    try {
        message.setFrom(config.emailSettings.emailUsername)
    } catch (e: MessagingException) {
        throw RuntimeException("Failed to set from address: ", e)
    }
    message.addRecipient(MimeMessage.RecipientType.TO, InternetAddress(recipient))
    message.subject = subject
    message.setText(body)
    message.setRecipients(MimeMessage.RecipientType.TO, recipient)
    val transport: Transport = session.getTransport("smtp")
    transport.connect(config.emailSettings.emailHost, config.emailSettings.emailUsername, config.emailSettings.emailPassword)
    transport.sendMessage(message, message.allRecipients)
    transport.close()
}

fun sendEmailVerification(context: Context, user: User) {
    val emailToken = generateSalt(config.securitySettings.saltSize*2)
    val saltHash: SaltHash = hashPassword(emailToken)
    user.emailVerified=false
    user.hashedEmailToken = saltHash.hash
    user.emailTokenSalt = saltHash.salt
    user.emailTokenExpiryUnix = Instant.now().epochSecond + config.securitySettings.authorizationExpiry
    sendEmail(user.email, "Email Verification", "Please click the following link to verify your email: ${config.webUrl}/verifyEmail?token=$emailToken\nAny previous email verification links are no longer valid")

}
