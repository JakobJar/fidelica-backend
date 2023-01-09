package org.fidelica.backend.user.group;

import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.HashSet;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
public class StandardGroup implements Group {

    @BsonId
    private final ObjectId id;
    private final String name;
    private final Collection<String> permissions;

    public StandardGroup(ObjectId id, String name) {
        this(id, name, new HashSet<>());
    }

    public StandardGroup(ObjectId id, String name, Collection<String> permissions) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(permissions);

        this.id = id;
        this.name = name;
        this.permissions = permissions;
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
