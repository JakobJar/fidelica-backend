package org.fidelica.backend.user.permission;

import org.fidelica.backend.user.User;

public interface UserPermissionProcessor {

    boolean hasPermission(User user, String permission);
}
