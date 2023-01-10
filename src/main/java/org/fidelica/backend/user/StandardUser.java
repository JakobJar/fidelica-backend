package org.fidelica.backend.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import org.fidelica.backend.user.login.PasswordHash;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashSet;

@Data
@EqualsAndHashCode(of = "id")
public class StandardUser implements User {

    @BsonId
    private final ObjectId id;
    private final String name;
    private final String email;
    private final PasswordHash passwordHash;
    private final Collection<ObjectId> groupIds;

    private final Collection<String> permissions;

    public StandardUser(ObjectId id, String name, String email, PasswordHash passwordHash) {
        this(id, name, email, passwordHash, new HashSet<>(), new HashSet<>());
    }

    @BsonCreator
    public StandardUser(@NonNull ObjectId id, @NonNull String name, @NonNull String email,
                        @NonNull PasswordHash passwordHash, @NonNull Collection<ObjectId> groupIds,
                        @NonNull Collection<String> permissions) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.groupIds = groupIds;
        this.permissions = permissions;
    }

    @Override
    public LocalDateTime getCreationDateTime() {
        return id.getDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    @Override
    public void addPermission(@NonNull String permission) {
        permissions.add(permission);
    }

    @Override
    public void removePermission(@NonNull String permission) {
        permissions.remove(permission);
    }

    @Override
    public boolean hasPermission(@NonNull String permission) {
        return permissions.contains(permission);
    }
}
