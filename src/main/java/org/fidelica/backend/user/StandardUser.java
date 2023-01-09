package org.fidelica.backend.user;

import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashSet;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
public class StandardUser implements User {

    @BsonId
    private final ObjectId id;
    private final String name;
    private final String email;
    private final String passwordHash;
    private final Collection<ObjectId> groupIds;

    private final Collection<String> permissions;

    public StandardUser(ObjectId id, String name, String email, String passwordHash) {
        this(id, name, email, passwordHash, new HashSet<>(), new HashSet<>());
    }

    public StandardUser(ObjectId id, String name, String email, String passwordHash, Collection<ObjectId> groupIds, Collection<String> permissions) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(email);
        Preconditions.checkNotNull(passwordHash);
        Preconditions.checkNotNull(groupIds);
        Preconditions.checkNotNull(permissions);

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
    public void addPermission(String permission) {
        permissions.add(permission);
    }

    @Override
    public void removePermission(String permission) {
        permissions.remove(permission);
    }

    @Override
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
}
