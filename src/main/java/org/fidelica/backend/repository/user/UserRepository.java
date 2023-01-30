package org.fidelica.backend.repository.user;

import org.bson.types.ObjectId;
import org.fidelica.backend.user.User;

import java.util.Optional;

public interface UserRepository {

    void create(User User);

    Optional<User> findById(ObjectId id);

    boolean isUserNameExisting(String username);

    boolean isEmailExisting(String email);

    Optional<User> findByUserNameOrEmail(String search);
}
