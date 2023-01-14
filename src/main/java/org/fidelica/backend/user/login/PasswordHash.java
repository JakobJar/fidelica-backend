package org.fidelica.backend.user.login;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.io.Serializable;

@BsonDiscriminator
public interface PasswordHash extends Serializable {

    byte[] getSalt();

    byte[] getHash();
}
