package org.fidelica.backend.user.login;

import java.security.spec.InvalidKeySpecException;

public interface PasswordHandler {

    PasswordHash generateHash(String password) throws InvalidKeySpecException;

    PasswordHash generateHash(String password, byte[] salt) throws InvalidKeySpecException;
}
