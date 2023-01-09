package org.fidelica.backend.user;

import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StandardUserManager implements UserManager {

    private final Map<ObjectId, User> users;

    public StandardUserManager() {
        this.users = new HashMap<>();
    }

    @Override
    public void register(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public void unregister(User user) {
        users.remove(user.getId(), user);
    }

    @Override
    public Optional<User> getById(ObjectId id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Collection<User> getEntities() {
        return users.values();
    }
}
