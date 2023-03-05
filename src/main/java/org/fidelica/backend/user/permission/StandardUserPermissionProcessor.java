package org.fidelica.backend.user.permission;

import com.google.inject.Inject;
import lombok.NonNull;
import org.fidelica.backend.user.User;
import org.fidelica.backend.user.group.GroupManager;

import java.util.Optional;

public class StandardUserPermissionProcessor implements UserPermissionProcessor {

    private final GroupManager groupManager;

    @Inject
    public StandardUserPermissionProcessor(@NonNull GroupManager groupManager) {
        this.groupManager = groupManager;
    }

    @Override
    public boolean hasPermission(User user, @NonNull String permission) {
        if (user == null)
            return false;

        var lowerPermission = permission.toLowerCase();
        if (user.hasPermission(lowerPermission))
            return true;

        return user.getGroupIds().stream()
                .map(groupManager::getById)
                .flatMap(Optional::stream)
                .anyMatch(group -> group.hasPermission(lowerPermission));
    }
}
