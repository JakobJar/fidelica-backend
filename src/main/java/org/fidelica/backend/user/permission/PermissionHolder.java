package org.fidelica.backend.user.permission;

import java.util.Collection;

public interface PermissionHolder {

    void addPermission(String permission);

    void removePermission(String permission);

    boolean hasPermission(String permission);

    Collection<String> getPermissions();
}
