package org.fidelica.backend.user.login;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@ToString
@EqualsAndHashCode
@Getter
@BsonDiscriminator("SaltedPasswordHash")
public class SaltedPasswordHash implements PasswordHash {

    private final byte[] salt;
    private final byte[] hash;

    @BsonCreator
    public SaltedPasswordHash(@BsonProperty("salt") byte[] salt, @BsonProperty("hash") byte[] hash) {
        this.salt = salt;
        this.hash = hash;
    }
}
