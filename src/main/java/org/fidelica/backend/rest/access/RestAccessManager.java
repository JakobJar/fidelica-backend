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
        User user = context.sessionAttribute("user");
        if (user == null && !roles.contains(AccessAuthenticationRole.ANONYMOUS)) {
            throw new UnauthorizedResponse("Not logged in");
        }

        handler.handle(context);
    }
}
