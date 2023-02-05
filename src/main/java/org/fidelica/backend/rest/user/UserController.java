package org.fidelica.backend.rest.user;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UnauthorizedResponse;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.fidelica.backend.repository.user.UserRepository;
import org.fidelica.backend.user.User;

public record UserController(UserRepository repository) {

    public void getCurrentUser(@NonNull Context context) {
        var user = context.<User>sessionAttribute("user");
        if (user == null)
            throw new UnauthorizedResponse("Not logged in");
        context.json(user);
    }

    public void getUserById(@NonNull Context context) {
        ObjectId id;
        try {
            id = new ObjectId(context.pathParam("id"));
        } catch (IllegalArgumentException e) {
            throw new BadRequestResponse(e.getMessage());
        }

        repository.findById(id).ifPresentOrElse(context::json, () -> {
            throw new NotFoundResponse("User not found");
        });
    }
}
