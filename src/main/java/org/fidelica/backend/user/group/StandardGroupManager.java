package org.fidelica.backend.user.group;

import com.google.inject.Inject;
import lombok.NonNull;
import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@NonNull
public class StandardGroupManager implements GroupManager {

    private final Map<ObjectId, Group> groups;

    @Inject
    public StandardGroupManager() {
        this(new HashMap<>());
    }

    public StandardGroupManager(Map<ObjectId, Group> groups) {
        this.groups = groups;
    }

    @Override
    public void register(Group group) {
        groups.put(group.getId(), group);
    }

    @Override
    public void unregister(Group group) {
        groups.remove(group.getId());
    }

    @Override
    public Optional<Group> getById(ObjectId id) {
        return Optional.ofNullable(groups.get(id));
    }

    @Override
    public Collection<Group> getEntities() {
        return groups.values();
    }
}
