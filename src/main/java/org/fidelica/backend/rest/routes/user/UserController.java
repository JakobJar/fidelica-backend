package org.fidelica.backend.rest.routes.user;

import com.google.inject.Inject;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.fidelica.backend.repository.repositories.user.UserRepository;
import org.fidelica.backend.user.User;

public class UserController {

    private final UserRepository repository;

    @Inject
    public UserController(@NonNull UserRepository repository) {
        this.repository = repository;
    }

    public void getCurrentUser(@NonNull Context context) {
        User user = context.<User>sessionAttribute("user");
        context.json(user);
    }

    public void getUserById(@NonNull Context context) {
        ObjectId id;
        try {
            id = new ObjectId(context.pathParam("id"));
        } catch (IllegalArgumentException e) {
            throw new BadRequestResponse(e.getMessage());
        }
        boolean preview = context.queryParamAsClass("preview", Boolean.class).getOrDefault(false);

        if (preview) {
            repository.findPreviewById(id).ifPresentOrElse(context::json, () -> {
                throw new NotFoundResponse("User not found.");
            });
            return;
        }

        repository.findById(id).ifPresentOrElse(context::json, () -> {
            throw new NotFoundResponse("User not found.");
        });
    }
}
