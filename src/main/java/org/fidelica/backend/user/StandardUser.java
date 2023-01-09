package org.fidelica.backend.user;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
public class StandardUser implements User {

    @BsonId
    private final ObjectId id;
    private final String name;
    private final String email;
    private final String passwordHash;

    public StandardUser(ObjectId id, String name, String email, String passwordHash) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(email);
        Preconditions.checkNotNull(passwordHash);

        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    @Override
    public LocalDateTime getCreationDateTime() {
        return id.getDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
