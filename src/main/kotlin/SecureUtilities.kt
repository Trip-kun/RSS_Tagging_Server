package org.example

import at.favre.lib.crypto.bcrypt.BCrypt
import java.security.SecureRandom
import java.util.*

private val random: Random = SecureRandom()
private val hasher: BCrypt.Hasher = BCrypt.withDefaults()
private val verifier: BCrypt.Verifyer = BCrypt.verifyer()
private val config: Config = Config.getConfig()
data class SaltHash(val salt: String, val hash: String)
fun generateSalt(length: Int): String {
    val salt: StringBuilder = StringBuilder()
    for (i in 0 until length) {
        salt.append((random.nextInt(26) + 'a'.code).toChar())
    }
    return salt.toString()
}
fun hashPassword(password: String): SaltHash {
    val salt: String = generateSalt(config.securitySettings.saltSize)
    val hash: String = hasher.hashToString(config.securitySettings.hashCost, (salt + password).toCharArray())
    return SaltHash(salt, hash)
}
fun verifyPassword(password: String, saltHash: SaltHash): Boolean {
    return verifier.verify((saltHash.salt + password).toCharArray(), saltHash.hash).verified
}