package org.fidelica.backend.user.group;

import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class StandardGroupManager implements GroupManager {

    private final Map<ObjectId, Group> groups;

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
