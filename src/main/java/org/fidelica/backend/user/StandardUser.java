package org.fidelica.backend.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.bson.codecs.pojo.annotations.*;
import org.bson.types.ObjectId;
import org.fidelica.backend.rest.json.Exclude;
import org.fidelica.backend.user.login.PasswordHash;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashSet;

@Data
@EqualsAndHashCode(of = "id")
@BsonDiscriminator("StandardUser")
public class StandardUser implements User {

    @BsonId
    private final ObjectId id;
    private final String name;
    @Exclude
    private final String email;
    @Exclude
    private final PasswordHash passwordHash;
    private final Collection<ObjectId> groupIds;
    private final String avatarUrl;

    private final Collection<String> permissions;

    private boolean emailVerified;

    public StandardUser(ObjectId id, String name, String email, PasswordHash passwordHash) {
        this(id, name, email, passwordHash, new HashSet<>(), new HashSet<>(), null, false);
    }

    @BsonCreator
    public StandardUser(@NonNull @BsonId ObjectId id, @NonNull @BsonProperty("name") String name,
                        @NonNull @BsonProperty("email") String email, @NonNull @BsonProperty("passwordHash") PasswordHash passwordHash,
                        @NonNull @BsonProperty("groupIds") Collection<ObjectId> groupIds,
                        @NonNull @BsonProperty("permissions") Collection<String> permissions, @BsonProperty("avatarURL") String avatarUrl,
                        @BsonProperty("emailVerified") boolean emailVerified) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.groupIds = groupIds;
        this.permissions = permissions;
        this.avatarUrl = avatarUrl;
        this.emailVerified = emailVerified;
    }

    @Override
    @BsonIgnore
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
