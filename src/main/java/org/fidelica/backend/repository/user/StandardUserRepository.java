package org.fidelica.backend.repository.user;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import lombok.NonNull;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.fidelica.backend.user.User;

import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

public class StandardUserRepository implements UserRepository {

    private static final Bson PREVIEW_PROJECTION = Projections.include("_t", "_id", "name", "avatarUrl");

    private final MongoCollection<User> users;

    @Inject
    public StandardUserRepository(@NonNull MongoDatabase database) {
        this.users = database.getCollection("users", User.class);

        var nameEmailIndexOptions = new IndexOptions()
                .unique(true)
                .collation(Collation.builder()
                        .locale("en")
                        .collationStrength(CollationStrength.SECONDARY)
                        .build());
        this.users.createIndex(Indexes.ascending("name"),nameEmailIndexOptions);
        this.users.createIndex(Indexes.ascending("email"),nameEmailIndexOptions);
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
    public Optional<User> findPreviewById(@NonNull ObjectId id) {
        return Optional.ofNullable(users.find(eq("_id", id)).projection(PREVIEW_PROJECTION).first());
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
