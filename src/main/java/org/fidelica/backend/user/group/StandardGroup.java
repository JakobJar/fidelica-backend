package org.fidelica.backend.user.group;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.HashSet;

@Data
@EqualsAndHashCode(of = "id")
@BsonDiscriminator("StandardGroup")
public class StandardGroup implements Group {

    @BsonId
    private final ObjectId id;
    private final String name;
    private final Collection<String> permissions;

    public StandardGroup(ObjectId id, String name) {
        this(id, name, new HashSet<>());
    }

    public StandardGroup(@NonNull ObjectId id, @NonNull String name, @NonNull Collection<String> permissions) {
        this.id = id;
        this.name = name;
        this.permissions = permissions;
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
