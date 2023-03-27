package org.fidelica.backend.rest.routes.article;

import com.google.inject.Inject;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UnauthorizedResponse;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.fidelica.backend.article.ArticleRating;
import org.fidelica.backend.article.history.ArticleEdit;
import org.fidelica.backend.article.history.ComputedArticleEdit;
import org.fidelica.backend.article.history.StandardArticleEdit;
import org.fidelica.backend.article.history.difference.TextDifferenceProcessor;
import org.fidelica.backend.repository.repositories.article.ArticleEditRepository;
import org.fidelica.backend.repository.repositories.article.ArticleRepository;
import org.fidelica.backend.user.User;
import org.fidelica.backend.user.permission.UserPermissionProcessor;

import java.util.regex.Pattern;


public class ArticleEditController {

    private final ArticleRepository articleRepository;
    private final ArticleEditRepository editRepository;
    private final TextDifferenceProcessor textDifferenceProcessor;
    private final UserPermissionProcessor permissionProcessor;

    private final Pattern textPattern;

    @Inject
    public ArticleEditController(@NonNull ArticleRepository articleRepository,
                                 @NonNull ArticleEditRepository editRepository,
                                 @NonNull TextDifferenceProcessor textDifferenceProcessor,
                                 @NonNull UserPermissionProcessor permissionProcessor) {
        this.articleRepository = articleRepository;
        this.editRepository = editRepository;
        this.textDifferenceProcessor = textDifferenceProcessor;
        this.permissionProcessor = permissionProcessor;

        this.textPattern = Pattern.compile("^[\\x00-\\x7F]*$");
    }

    public void createEdit(@NonNull Context context) {
        ObjectId articleId;
        try {
            articleId = new ObjectId(context.pathParam("articleId"));
        } catch (IllegalArgumentException e) {
            throw new BadRequestResponse(e.getMessage());
        }

        var title = context.formParam("title");
        var shortDescription = context.formParam("shortDescription");
        var rawRating = context.formParam("rating");
        var content = context.formParam("content");
        var description = context.formParam("description");

        if (title == null || shortDescription == null || rawRating == null || content == null || description == null)
            throw new BadRequestResponse("Invalid form data.");

        title = title.trim();
        shortDescription = shortDescription.trim();
        rawRating = rawRating.trim();
        content = content.trim();
        description = description.trim();

        if (!textPattern.matcher(title).matches())
            throw new BadRequestResponse("Title contains invalid characters.");

        ArticleRating rating;
        try {
            rating = ArticleRating.valueOf(rawRating.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestResponse("Invalid rating.");
        }

        if (!textPattern.matcher(shortDescription).matches())
            throw new BadRequestResponse("Claim contains invalid characters.");

        if (!textPattern.matcher(content).matches())
            throw new BadRequestResponse("Content contains invalid characters.");

        User user = context.sessionAttribute("user");
        if (!permissionProcessor.hasPermission(user, "article.edit"))
            throw new UnauthorizedResponse("You do not have permission to edit fact checks.");

        var article = articleRepository.findById(articleId).orElseThrow(() -> new NotFoundResponse("Article not found."));

        if ((!article.isVisible() || !article.isEditable())
                && !permissionProcessor.hasPermission(user, "article.ignoreditable"))
            throw new BadRequestResponse("Article is not editable.");

        String newTitle = null;
        String newClaim = null;
        ArticleRating newRating = null;

        if (!article.getTitle().equals(title))
            newTitle = title;

        if (!article.getRating().equals(rating))
            newRating = rating;

        if (!article.getShortDescription().equals(shortDescription))
            newClaim = shortDescription;

        var contentChanges = textDifferenceProcessor.getDifference(article.getContent(), content);

        if (newTitle == null && newClaim == null && newRating == null && contentChanges.isEmpty())
            throw new BadRequestResponse("No changes compared to current version.");

        var edit = new StandardArticleEdit(ObjectId.get(), articleId, description, newTitle, newClaim, newRating, contentChanges, user.getId());
        editRepository.create(edit);

        context.json(edit);
    }

    public void getEditPreviews(@NonNull Context context) {
        ObjectId articleId;
        try {
            articleId = new ObjectId(context.pathParam("articleId"));
        } catch (IllegalArgumentException e) {
            throw new BadRequestResponse(e.getMessage());
        }

        var page = context.queryParamAsClass("page", Integer.class).getOrDefault(0);
        var limit = context.queryParamAsClass("limit", Integer.class).getOrDefault(10);

        if (page < 0 || limit < 0 || limit > 10)
            throw new BadRequestResponse("Invalid page or limit.");

        // TODO: Check permission.
        var articlePreviews = editRepository.getPreviews(articleId, page, limit);
        context.json(articlePreviews);
    }

    public void getEditById(@NonNull Context context) {
        ObjectId articleId;
        ObjectId editId;
        try {
            articleId = new ObjectId(context.pathParam("articleId"));
            editId = new ObjectId(context.pathParam("editId"));
        } catch (IllegalArgumentException e) {
            throw new BadRequestResponse(e.getMessage());
        }

        var article = articleRepository.findById(articleId).orElseThrow(() -> new NotFoundResponse("Article not found."));
        var edit = editRepository.findById(editId).orElseThrow(() -> new NotFoundResponse("Edit not found."));

        var editsAfter = editRepository.getDifferencesAfter(articleId, editId);

        var newContent = article.getContent();
        for (ArticleEdit editAfter : editsAfter) {
            newContent = textDifferenceProcessor.applyDifferences(newContent, editAfter.getDifferences());
        }
        var oldContent = textDifferenceProcessor.applyDifferences(newContent, edit.getDifferences());

        var computedEdit = new ComputedArticleEdit(edit, oldContent, newContent);
        context.json(computedEdit);
    }
}
