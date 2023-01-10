package org.fidelica.backend.repository.user;

import org.fidelica.backend.repository.Repository;
import org.fidelica.backend.user.User;

public interface UserRepository extends Repository<User> {

    boolean isUserNameExisting(String username);

    boolean isEmailExisting(String email);
}
