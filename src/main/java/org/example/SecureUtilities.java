package org.example;

import at.favre.lib.crypto.bcrypt.BCrypt;
import kotlin.Pair;

import java.security.SecureRandom;
import java.util.Random;

public class SecureUtilities {
    private static final Random random = new SecureRandom();
    private static final BCrypt.Hasher hasher = BCrypt.withDefaults();
    private static final BCrypt.Verifyer verifier = BCrypt.verifyer();
    private static final Config config = Config.getConfig();
    public static String generateSalt(int length) {
        StringBuilder salt = new StringBuilder();
        for (int i = 0; i < length; i++) {
            salt.append((char) (random.nextInt(26) + 'a'));
        }
        return salt.toString();
    }
    public static Pair<String, String> hash(String password) {
        String salt = generateSalt(config.saltSize);
        return new Pair<>(hasher.hashToString(config.hashCost, (salt + password).toCharArray()), salt);
    }
    public static boolean verifyHash(String password, String salt, String hashedPassword) {
        return verifier.verify((salt + password).toCharArray(), hashedPassword).verified;
    }
}
