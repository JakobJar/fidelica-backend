package org.fidelica.backend.user.login;

import lombok.NonNull;

import java.security.spec.InvalidKeySpecException;

public interface PasswordHandler {

    PasswordHash generateHash(String password) throws InvalidKeySpecException;

    PasswordHash generateHash(String password, byte[] salt) throws InvalidKeySpecException;

    boolean validatePassword(@NonNull PasswordHash storedPassword, @NonNull String password) throws InvalidKeySpecException ;
}
