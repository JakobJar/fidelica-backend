package org.fidelica.backend.rest.routes.moderation;

import com.google.inject.Inject;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ConflictResponse;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.fidelica.backend.repository.repositories.article.ArticleRepository;
import org.fidelica.backend.user.User;
import org.fidelica.backend.user.permission.UserPermissionProcessor;

public class ArticleModerationController {

    private final ArticleRepository articleRepository;
    private final UserPermissionProcessor permissionProcessor;

    @Inject
    public ArticleModerationController(@NonNull ArticleRepository articleRepository,
                                       @NonNull UserPermissionProcessor permissionProcessor) {
        this.articleRepository = articleRepository;
        this.permissionProcessor = permissionProcessor;
    }

    public void getPendingEdits(@NonNull Context context) {
        var page = context.queryParamAsClass("page", Integer.class).getOrDefault(0);
        var limit = context.queryParamAsClass("limit", Integer.class).getOrDefault(10);

        if (page < 0 || limit < 0 || limit > 10)
            throw new BadRequestResponse("Invalid page or limit.");

        User user = context.sessionAttribute("user");
        if (!permissionProcessor.hasPermission(user, "article.moderate"))
            throw new UnauthorizedResponse("You are not permitted to moderate articles.");

        var pendingEdits = articleRepository.getUncheckedEditPreviews(page, limit);
        context.json(pendingEdits);
    }

    public void checkEdit(@NonNull Context context) {
        ObjectId editId;
        try {
            editId = new ObjectId(context.pathParam("editId"));
        } catch (IllegalArgumentException e) {
            throw new BadRequestResponse(e.getMessage());
        }

        var approve = context.formParamAsClass("approve", Boolean.class)
                .getOrThrow(unused -> new BadRequestResponse("Invalid form data."));
        var comment = context.formParam("comment");
        if (comment == null)
            throw new BadRequestResponse("Invalid form data.");

        User user = context.sessionAttribute("user");
        if (!permissionProcessor.hasPermission(user, "article.edit.check"))
            throw new UnauthorizedResponse("You're not permitted to approve edits.");

        var success = articleRepository.checkEdit(editId, approve, user.getId(), comment);
        if (!success)
            throw new ConflictResponse("Edit wasn't found or is already checked.");

        context.json("Success");
    }
}
