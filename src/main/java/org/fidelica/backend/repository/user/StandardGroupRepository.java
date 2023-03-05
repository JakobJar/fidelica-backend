package org.fidelica.backend.repository.user;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.NonNull;
import org.fidelica.backend.user.group.Group;

import java.util.ArrayList;
import java.util.Collection;

public class StandardGroupRepository implements GroupRepository {

    private final MongoCollection<Group> groups;

    @Inject
    public StandardGroupRepository(@NonNull MongoDatabase database) {
        this.groups = database.getCollection("groups", Group.class);
    }

    @Override
    public void create(Group group) {
        this.groups.insertOne(group);
    }

    @Override
    public Collection<Group> findAll() {
        return groups.find().into(new ArrayList<>());
    }
}
