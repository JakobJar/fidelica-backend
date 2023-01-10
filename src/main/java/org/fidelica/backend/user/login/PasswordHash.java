package org.fidelica.backend.user.login;

public interface PasswordHash {

    byte[] getSalt();

    byte[] getHash();
}
