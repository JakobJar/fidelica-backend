package org.fidelica.backend.rest.routes.moderation;

import com.google.inject.Inject;
import io.javalin.http.*;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.fidelica.backend.article.history.difference.TextDifferenceProcessor;
import org.fidelica.backend.repository.repositories.article.ArticleEditRepository;
import org.fidelica.backend.repository.repositories.article.ArticleRepository;
import org.fidelica.backend.user.User;
import org.fidelica.backend.user.permission.UserPermissionProcessor;
import org.fidelica.backend.util.LockMap;

public class ArticleModerationController {

    private final ArticleRepository articleRepository;
    private final ArticleEditRepository editRepository;
    private final TextDifferenceProcessor differenceProcessor;
    private final UserPermissionProcessor permissionProcessor;

    private final LockMap<ObjectId> editLocks;

    @Inject
    public ArticleModerationController(@NonNull ArticleRepository articleRepository,
                                       @NonNull ArticleEditRepository editRepository,
                                       @NonNull TextDifferenceProcessor differenceProcessor,
                                       @NonNull UserPermissionProcessor permissionProcessor) {
        this.articleRepository = articleRepository;
        this.editRepository = editRepository;
        this.differenceProcessor = differenceProcessor;
        this.permissionProcessor = permissionProcessor;

        this.editLocks = new LockMap<>();
    }

    public void getPendingEdits(@NonNull Context context) {
        var page = context.queryParamAsClass("page", Integer.class).getOrDefault(0);
        var limit = context.queryParamAsClass("limit", Integer.class).getOrDefault(10);

        if (page < 0 || limit < 0 || limit > 10)
            throw new BadRequestResponse("Invalid page or limit.");

        User user = context.sessionAttribute("user");
        if (!permissionProcessor.hasPermission(user, "article.moderate"))
            throw new UnauthorizedResponse("You are not permitted to moderate articles.");

        var pendingEdits = editRepository.getUncheckedPreviews(page, limit);
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
        if (!permissionProcessor.hasPermission(user, "article.edit.check"))
            throw new UnauthorizedResponse("You're not permitted to approve edits.");

        editLocks.lock(editId);
        try {
            var success = editRepository.check(editId, approve, user.getId(), comment);
            if (!success)
                throw new ConflictResponse("Edit wasn't found or is already checked.");

            if (approve) {
                var edit = editRepository.findById(editId).orElseThrow(() -> new NotFoundResponse("Edit not found."));

                if (editRepository.isFirst(edit.getArticleId(), editId)) {
                    articleRepository.updateVisibility(edit.getArticleId(), true);
                } else {
                    editRepository.disproveOtherEdits(edit.getArticleId(), editId, user.getId());

                    var article = articleRepository.findById(edit.getArticleId()).orElseThrow(() -> new NotFoundResponse("Article not found."));

                    var newContent = differenceProcessor.applyDifferences(article.getContent(), edit.getDifferences());
                    var newDifferences = differenceProcessor.getDifference(newContent, article.getContent());

                    articleRepository.update(article.getId(), edit.getTitle(), edit.getShortDescription(), newContent);
                    editRepository.updateDifferences(editId, newDifferences);
                }

                context.json(edit);
            }
        } finally {
            editLocks.unlock(editId);
        }
    }
}
