package org.fidelica.backend.rest.routes.moderation;

import com.google.inject.Inject;
import io.javalin.http.*;
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

    public void checkEdit(@NonNull Context context) {
        ObjectId editId;
        try {
            editId = new ObjectId(context.pathParam("editId"));
        } catch (IllegalArgumentException e) {
            throw new BadRequestResponse(e.getMessage());
        }

        boolean approve = context.formParamAsClass("approve", Boolean.class)
                .getOrThrow(unused -> new BadRequestResponse("Invalid form data."));
        var comment = context.formParam("comment");
        if (comment == null)
            throw new BadRequestResponse("Invalid form data.");

        User user = context.sessionAttribute("user");
        if (!permissionProcessor.hasPermission(user, "post.edit.check"))
            throw new UnauthorizedResponse("You're not permitted to approve edits.");

        editLocks.lock(editId);
        try {
            var success = postRepository.checkEdit(editId, approve, user.getId(), comment);
            if (!success)
                throw new ConflictResponse("Edit wasn't found or is already checked.");

            var edit = postRepository.findCheckEditById(editId).orElseThrow(() -> new NotFoundResponse("Edit not found."));

            if (approve) {
                if (postRepository.isFirstEdit(edit.getPostId(), editId)) {
                    postRepository.updateVisibility(edit.getPostId(), true);
                } else {
                    postRepository.update(edit.getPostId(), edit.getNote(), edit.getRating(), edit.getRelatedArticles());
                }
            }

            context.json(edit);
        } finally {
            editLocks.unlock(editId);
        }
    }
}
