package org.fidelica.backend.repository.user;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.fidelica.backend.user.User;

import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

public class StandardUserRepository implements UserRepository {

    private final MongoCollection<User> users;

    public StandardUserRepository(@NonNull MongoDatabase database) {
        this.users = database.getCollection("users", User.class);
    }

    @Override
    public void create(@NonNull User entity) {
        users.insertOne(entity);
    }

    @Override
    public Optional<User> findById(@NonNull ObjectId id) {
        return Optional.ofNullable(users.find(eq("_id", id)).first());
    }

    @Override
    public boolean isUserNameExisting(@NonNull String username) {
        return users.countDocuments(eq("name", username)) > 0;
    }

    @Override
    public boolean isEmailExisting(@NonNull String email) {
        return users.countDocuments(eq("email", email)) > 0;
    }

    @Override
    public Optional<User> findByUserNameOrEmail(@NonNull String search) {
        return Optional.ofNullable(users.find(or(eq("name", search), eq("email", search))).first());
    }
}
