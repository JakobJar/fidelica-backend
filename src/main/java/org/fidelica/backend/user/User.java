package org.fidelica.backend.user;

import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public interface User {

    ObjectId getId();

    String getName();

    String getEmail();

    String getPasswordHash();

    LocalDateTime getCreationDateTime();
}
