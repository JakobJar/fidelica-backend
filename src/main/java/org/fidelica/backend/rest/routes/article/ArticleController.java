package org.fidelica.backend.rest.routes.article;

import com.google.inject.Inject;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UnauthorizedResponse;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.fidelica.backend.article.ArticleRating;
import org.fidelica.backend.article.StandardArticle;
import org.fidelica.backend.article.history.StandardArticleEdit;
import org.fidelica.backend.article.history.difference.TextDifferenceProcessor;
import org.fidelica.backend.repository.repositories.article.ArticleRepository;
import org.fidelica.backend.user.User;
import org.fidelica.backend.user.permission.UserPermissionProcessor;

import java.util.Locale;
import java.util.regex.Pattern;

public class ArticleController {

    private final ArticleRepository repository;
    private final TextDifferenceProcessor textDifferenceProcessor;
    private final UserPermissionProcessor permissionProcessor;

    private final Pattern textPattern;

    @Inject
    public ArticleController(@NonNull ArticleRepository repository, @NonNull TextDifferenceProcessor textDifferenceProcessor,
                             @NonNull UserPermissionProcessor permissionProcessor) {
        this.repository = repository;
        this.textDifferenceProcessor = textDifferenceProcessor;
        this.permissionProcessor = permissionProcessor;

        this.textPattern = Pattern.compile("^[\\x00-\\x7F]*$");
    }

    public void createArticle(@NonNull Context context) {
        var title = context.formParam("title");
        var shortDescription = context.formParam("shortDescription");
        var rawRating = context.formParam("rating");
        var content = context.formParam("content");
        var language = context.formParam("language");

        if (title == null || shortDescription == null || rawRating == null || content == null || language == null)
            throw new BadRequestResponse("Invalid form data.");

        title = title.trim();
        shortDescription = shortDescription.trim();
        rawRating = rawRating.trim();
        content = content.trim();
        language = language.trim();

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
        if (!permissionProcessor.hasPermission(user, "article.create"))
            throw new UnauthorizedResponse("You do not have permission to create articles.");

        // TODO: Check language is valid.
        var article = new StandardArticle(ObjectId.get(), title, shortDescription, rating, content, Locale.forLanguageTag(language));

        var difference = textDifferenceProcessor.getDifference(content, "");
        var firstEdit = new StandardArticleEdit(ObjectId.get(), article.getId(), "Create article.", title, shortDescription, rating, difference, user.getId());

        repository.create(article, firstEdit);
        context.json(article);
    }

    public void searchArticles(@NonNull Context context) {
        var query = context.queryParamAsClass("query", String.class)
                .getOrThrow(unused -> new BadRequestResponse("Query required"));
        var language = context.queryParamAsClass("language", String.class).getOrDefault("en-us");

        var locale = Locale.forLanguageTag(language);
        var articles = repository.search(query, locale);

        context.json(articles);
    }

    public void getArticleById(@NonNull Context context) {
        var preview = context.queryParamAsClass("preview", Boolean.class).getOrDefault(false);

        ObjectId id;
        try {
            id = new ObjectId(context.pathParam("articleId"));
        } catch (IllegalArgumentException e) {
            throw new BadRequestResponse(e.getMessage());
        }

        var articleOptional = preview ? repository.findPreviewById(id) : repository.findById(id);
        var article = articleOptional.orElseThrow(() -> new NotFoundResponse("Article not found."));

        User user = context.sessionAttribute("user");
        if (!permissionProcessor.hasPermission(user, "article.seehidden") && !article.isVisible())
            throw new NotFoundResponse("Article not found.");

        context.json(article);
    }
}
