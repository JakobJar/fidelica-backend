package org.fidelica.backend.user;

import lombok.*;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;
import org.fidelica.backend.rest.json.Exclude;
import org.fidelica.backend.user.login.PasswordHash;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashSet;

@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
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
    private String avatarUrl;

    private final Collection<String> permissions;

    public StandardUser(ObjectId id, String name, String email, PasswordHash passwordHash) {
        this(id, name, email, passwordHash, new HashSet<>(), new HashSet<>(), null);
    }

    public StandardUser(@NonNull ObjectId id, @NonNull String name, @NonNull String email,
                        @NonNull PasswordHash passwordHash, @NonNull Collection<ObjectId> groupIds,
                        @NonNull Collection<String> permissions, String avatarUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.groupIds = groupIds;
        this.permissions = permissions;
        this.avatarUrl = avatarUrl;
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
