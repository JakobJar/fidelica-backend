package org.fidelica.backend.repository.user;

import com.google.common.base.Preconditions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.fidelica.backend.user.StandardUser;

import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public class StandardUserRepository implements UserRepository<StandardUser> {

    private final MongoCollection<StandardUser> users;

    public StandardUserRepository(@NonNull MongoDatabase database) {
        this.users = database.getCollection("users", StandardUser.class);
    }

    @Override
    public void create(@NonNull StandardUser entity) {
        users.insertOne(entity);
    }

    @Override
    public Optional<StandardUser> findById(@NonNull ObjectId id) {
        Preconditions.checkNotNull(id);
        return Optional.ofNullable(users.find(eq("_id", id)).first());
    }
}
