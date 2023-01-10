package org.fidelica.backend.user.login;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class PBKDFPasswordHandler implements PasswordHandler {

    private final SecureRandom random;
    private final SecretKeyFactory keyFactory;

    public PBKDFPasswordHandler() throws NoSuchAlgorithmException {
        this.random = new SecureRandom();
        this.keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    }

    @Override
    public PasswordHash generateHash(String password) throws InvalidKeySpecException {
        byte[] salt = new byte[12];
        random.nextBytes(salt);
        return generateHash(password, salt);
    }

    @Override
    public PasswordHash generateHash(String password, byte[] salt) throws InvalidKeySpecException {
        var keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        return new SaltedPasswordHash(salt, keyFactory.generateSecret(keySpec).getEncoded());
    }
}
