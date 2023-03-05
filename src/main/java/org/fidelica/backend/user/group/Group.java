package org.fidelica.backend.user.group;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.fidelica.backend.repository.Identifiable;
import org.fidelica.backend.user.permission.PermissionHolder;

@BsonDiscriminator
public interface Group extends Identifiable, PermissionHolder {

    String getName();
}
