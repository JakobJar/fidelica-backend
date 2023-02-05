package org.fidelica.backend.rest.factcheck;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.fidelica.backend.factcheck.FactCheckRating;
import org.fidelica.backend.factcheck.StandardFactCheck;
import org.fidelica.backend.factcheck.history.StandardFactCheckEdit;
import org.fidelica.backend.factcheck.history.difference.TextDifferenceProcessor;
import org.fidelica.backend.repository.article.FactCheckRepository;
import org.fidelica.backend.user.User;

import java.util.Locale;
import java.util.regex.Pattern;

public class FactCheckController {

    private final FactCheckRepository repository;
    private final TextDifferenceProcessor textDifferenceProcessor;

    private final Pattern textPattern;

    public FactCheckController(FactCheckRepository repository, TextDifferenceProcessor textDifferenceProcessor) {
        this.repository = repository;
        this.textDifferenceProcessor = textDifferenceProcessor;

        this.textPattern = Pattern.compile("^[\\x00-\\x7F]*$");
    }

    public void createFactCheck(@NonNull Context context) {
        var title = context.formParam("title");
        var claim = context.formParam("claim");
        var rawRating = context.formParam("rating");
        var content = context.formParam("content");
        var language = context.formParam("language");

        if (title == null || claim == null || rawRating == null || content == null || language == null)
            throw new BadRequestResponse("Invalid form data.");

        if (!textPattern.matcher(title).matches())
            throw new BadRequestResponse("Title contains invalid characters.");

        FactCheckRating rating;
        try {
            rating = FactCheckRating.valueOf(rawRating.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestResponse("Invalid rating.");
        }

        if (!textPattern.matcher(claim).matches())
            throw new BadRequestResponse("Claim contains invalid characters.");

        if (!textPattern.matcher(content).matches())
            throw new BadRequestResponse("Content contains invalid characters.");

        // TODO: Check permission.
        User user = context.sessionAttribute("user");

        // TODO: Check language is valid.
        var article = new StandardFactCheck(ObjectId.get(), title, claim, rating, content, Locale.forLanguageTag(language));

        var difference = textDifferenceProcessor.getDifference("", content);
        var firstEdit = new StandardFactCheckEdit(ObjectId.get(), article.getId(), "Create article.", title, claim, rating, difference, user.getId());

        repository.create(article, firstEdit);
        context.json(article);
    }

    public void getFactCheckById(@NonNull Context context) {
        ObjectId id;
        try {
            id = new ObjectId(context.pathParam("id"));
        } catch (IllegalArgumentException e) {
            throw new BadRequestResponse(e.getMessage());
        }

        var factCheckOptional = repository.findById(id);
        if (factCheckOptional.isEmpty())
            throw new NotFoundResponse("Article not found.");

        var factCheck = factCheckOptional.get();
        // TODO: Check permission.
        if (!factCheck.isVisible())
            throw new NotFoundResponse("Article not found.");

        context.json(factCheck);
    }
}
