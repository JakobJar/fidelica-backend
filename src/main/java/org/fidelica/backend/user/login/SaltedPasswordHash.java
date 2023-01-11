package org.fidelica.backend.user.login;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@Data
@BsonDiscriminator("SaltedPasswordHash")
public class SaltedPasswordHash implements PasswordHash {

    private final byte[] salt;
    private final byte[] hash;
}
