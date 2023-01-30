package org.fidelica.backend.rest.article;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.fidelica.backend.article.StandardArticle;
import org.fidelica.backend.article.history.StandardArticleEdit;
import org.fidelica.backend.article.history.difference.TextDifferenceProcessor;
import org.fidelica.backend.repository.article.ArticleRepository;
import org.fidelica.backend.user.User;

import java.util.Locale;
import java.util.regex.Pattern;

public class ArticleController {

    private final ArticleRepository repository;
    private final TextDifferenceProcessor textDifferenceProcessor;

    private final Pattern textPattern;

    public ArticleController(ArticleRepository repository, TextDifferenceProcessor textDifferenceProcessor) {
        this.repository = repository;
        this.textDifferenceProcessor = textDifferenceProcessor;

        this.textPattern = Pattern.compile("^[\\x00-\\x7F]*$");
    }

    public void createArticle(@NonNull Context context) {
        var title = context.formParam("title");
        var shortDescription = context.formParam("shortDescription");
        var content = context.formParam("content");
        var language = context.formParam("language");

        if (title == null || shortDescription == null || content == null || language == null)
            throw new BadRequestResponse("Invalid form data.");

        if (!textPattern.matcher(title).matches())
            throw new BadRequestResponse("Invalid title.");

        if (!textPattern.matcher(shortDescription).matches())
            throw new BadRequestResponse("Invalid short description.");

        if (!textPattern.matcher(content).matches())
            throw new BadRequestResponse("Invalid content.");

        // TODO: Check permission.
        User user = context.sessionAttribute("user");

        // TODO: Check language is valid.
        var article = new StandardArticle(ObjectId.get(), title, shortDescription, content, Locale.forLanguageTag(language));

        var difference = textDifferenceProcessor.getDifference("", content);
        var firstEdit = new StandardArticleEdit(ObjectId.get(), article.getId(), "Create article.", difference, user.getId());

        repository.create(article, firstEdit);
        context.json(article);
    }
}
