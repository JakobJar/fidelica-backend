package org.fidelica.backend.user;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import org.fidelica.backend.user.group.GroupManager;
import org.fidelica.backend.user.group.StandardGroupManager;
import org.fidelica.backend.user.login.PBKDFPasswordHandler;
import org.fidelica.backend.user.login.PasswordHandler;
import org.fidelica.backend.user.permission.StandardUserPermissionProcessor;
import org.fidelica.backend.user.permission.UserPermissionProcessor;

public class UserModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(GroupManager.class).to(StandardGroupManager.class).in(Scopes.SINGLETON);
        bind(UserPermissionProcessor.class).to(StandardUserPermissionProcessor.class).in(Scopes.SINGLETON);
        bind(PasswordHandler.class).to(PBKDFPasswordHandler.class).in(Scopes.SINGLETON);
    }
}
