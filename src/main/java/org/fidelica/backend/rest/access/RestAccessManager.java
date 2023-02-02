package org.fidelica.backend.rest.access;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.security.AccessManager;
import io.javalin.security.RouteRole;
import org.fidelica.backend.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class RestAccessManager implements AccessManager {

    @Override
    public void manage(@NotNull Handler handler, @NotNull Context context, @NotNull Set<? extends RouteRole> roles) throws Exception {
        if (roles.isEmpty()) {
            handler.handle(context);
            return;
        }

        User user = context.sessionAttribute("user");
        if (user == null && !roles.contains(AccessRole.ANONYMOUS))
            throw new UnauthorizedResponse("You have to be logged in to access this resource.");
        if (user != null && !roles.contains(AccessRole.AUTHENTICATED))
            throw new UnauthorizedResponse("You have to be logged out to access this resource.");

        handler.handle(context);
    }
}
