package org.fidelica.backend.user.group;

import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.Optional;

public interface GroupManager {

    void register(Group entity);

    void unregister(Group entity);

    Optional<Group> getById(ObjectId id);

    Collection<Group> getEntities();
}
