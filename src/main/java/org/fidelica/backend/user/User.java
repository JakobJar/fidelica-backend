package org.fidelica.backend.user;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.types.ObjectId;
import org.fidelica.backend.repository.Identifiable;
import org.fidelica.backend.user.login.PasswordHash;
import org.fidelica.backend.user.permission.PermissionHolder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

@BsonDiscriminator
public interface User extends Identifiable, PermissionHolder, Serializable {

    String getName();

    String getEmail();

    PasswordHash getPasswordHash();

    LocalDateTime getCreationDateTime();

    Collection<ObjectId> getGroupIds();
}
