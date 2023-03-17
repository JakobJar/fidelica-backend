package org.fidelica.backend.user.group;

import com.google.inject.Inject;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.fidelica.backend.repository.repositories.user.GroupRepository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@NonNull
public class StandardGroupManager implements GroupManager {

    private final Map<ObjectId, Group> groups;

    private final GroupRepository repository;

    @Inject
    public StandardGroupManager(GroupRepository repository) {
        this.groups = new ConcurrentHashMap<>();
        this.repository = repository;
    }

    @Override
    public void reload() {
        groups.clear();
        repository.findAll().forEach(this::register);
    }

    @Override
    public void register(Group group) {
        groups.put(group.getTweetId(), group);
    }

    @Override
    public void unregister(Group group) {
        groups.remove(group.getTweetId());
    }

    @Override
    public Optional<Group> getById(ObjectId id) {
        return Optional.ofNullable(groups.get(id));
    }

    @Override
    public Collection<Group> getGroups() {
        return groups.values();
    }
}
