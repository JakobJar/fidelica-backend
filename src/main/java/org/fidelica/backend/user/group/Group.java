package org.fidelica.backend.user.group;

import org.fidelica.backend.repository.Identifiable;
import org.fidelica.backend.user.permission.PermissionHolder;

import java.util.Collection;

public interface Group extends Identifiable, PermissionHolder {

    String getName();

    Collection<String> getPermissions();
}
