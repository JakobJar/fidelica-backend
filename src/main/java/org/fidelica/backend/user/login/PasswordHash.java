package org.fidelica.backend.user.login;

import java.io.Serializable;

public interface PasswordHash extends Serializable {

    byte[] getSalt();

    byte[] getHash();
}
