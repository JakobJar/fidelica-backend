package org.fidelica.backend.user;

import org.bson.types.ObjectId;
import org.fidelica.backend.repository.Identifiable;
import org.fidelica.backend.user.permission.PermissionHolder;

import java.time.LocalDateTime;
import java.util.Collection;

public interface User extends Identifiable, PermissionHolder {

    String getName();

    String getEmail();

    String getPasswordHash();

    LocalDateTime getCreationDateTime();

    Collection<ObjectId> getGroupIds();
}
