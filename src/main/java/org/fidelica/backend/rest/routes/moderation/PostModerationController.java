package org.fidelica.backend.rest.routes.moderation;

import com.google.inject.Inject;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.fidelica.backend.repository.repositories.post.PostRepository;
import org.fidelica.backend.user.User;
import org.fidelica.backend.user.permission.UserPermissionProcessor;
import org.fidelica.backend.util.LockMap;

public class PostModerationController {

    private final PostRepository postRepository;
    private final UserPermissionProcessor permissionProcessor;

    private final LockMap<ObjectId> editLocks;

    @Inject
    public PostModerationController(@NonNull PostRepository postRepository,
                                    @NonNull UserPermissionProcessor permissionProcessor) {
        this.postRepository = postRepository;
        this.permissionProcessor = permissionProcessor;

        this.editLocks = new LockMap<>();
    }

    public void getPendingEdits(@NonNull Context context) {
        var page = context.queryParamAsClass("page", Integer.class).getOrDefault(0);
        var limit = context.queryParamAsClass("limit", Integer.class).getOrDefault(10);

        if (page < 0 || limit < 0 || limit > 10)
            throw new BadRequestResponse("Invalid page or limit.");

        User user = context.sessionAttribute("user");
        if (!permissionProcessor.hasPermission(user, "check.moderate"))
            throw new UnauthorizedResponse("You are not permitted to moderate checks.");

        var pendingEdits = postRepository.getUncheckedPostEdits();
        context.json(pendingEdits);
    }
}
