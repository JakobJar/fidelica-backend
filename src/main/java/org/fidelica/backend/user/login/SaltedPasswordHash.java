package org.fidelica.backend.user.login;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@Data
@BsonDiscriminator(value = "SaltedPasswordHash", key = "_cls")
public class SaltedPasswordHash implements PasswordHash {

    private final byte[] salt;
    private final byte[] hash;
}
