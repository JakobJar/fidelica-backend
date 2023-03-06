package org.fidelica.backend.rest.routes.factcheck;

import com.google.inject.Inject;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UnauthorizedResponse;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.fidelica.backend.factcheck.FactCheckRating;
import org.fidelica.backend.factcheck.history.ComputedFactCheckEdit;
import org.fidelica.backend.factcheck.history.FactCheckEdit;
import org.fidelica.backend.factcheck.history.StandardFactCheckEdit;
import org.fidelica.backend.factcheck.history.difference.TextDifferenceProcessor;
import org.fidelica.backend.repository.repositories.factcheck.FactCheckRepository;
import org.fidelica.backend.user.User;
import org.fidelica.backend.user.permission.UserPermissionProcessor;

import java.util.regex.Pattern;


public class FactCheckEditController {

    private final FactCheckRepository repository;
    private final TextDifferenceProcessor textDifferenceProcessor;
    private final UserPermissionProcessor permissionProcessor;

    private final Pattern textPattern;

    @Inject
    public FactCheckEditController(@NonNull FactCheckRepository repository,
                                   @NonNull TextDifferenceProcessor textDifferenceProcessor,
                                   @NonNull UserPermissionProcessor permissionProcessor) {
        this.repository = repository;
        this.textDifferenceProcessor = textDifferenceProcessor;
        this.permissionProcessor = permissionProcessor;

        this.textPattern = Pattern.compile("^[\\x00-\\x7F]*$");
    }

    public void createEdit(@NonNull Context context) {
        ObjectId factCheckId;
        try {
            factCheckId = new ObjectId(context.pathParam("factcheckId"));
        } catch (IllegalArgumentException e) {
            throw new BadRequestResponse(e.getMessage());
        }

        var title = context.formParam("title");
        var claim = context.formParam("claim");
        var rawRating = context.formParam("rating");
        var content = context.formParam("content");
        var description = context.formParam("description");

        if (title == null || claim == null || rawRating == null || content == null || description == null)
            throw new BadRequestResponse("Invalid form data.");

        title = title.trim();
        claim = claim.trim();
        rawRating = rawRating.trim();
        content = content.trim();
        description = description.trim();

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

        User user = context.sessionAttribute("user");
        if (!permissionProcessor.hasPermission(user, "factcheck.edit"))
            throw new UnauthorizedResponse("You do not have permission to edit fact checks.");

        var factCheck = repository.findById(factCheckId).orElseThrow(() -> new NotFoundResponse("FactCheck not found."));

        if (!factCheck.isVisible() && !factCheck.isEditable()
                && !permissionProcessor.hasPermission(user, "factcheck.ignoreditable"))
            throw new BadRequestResponse("FactCheck is not editable.");

        String newTitle = null;
        String newClaim = null;
        FactCheckRating newRating = null;

        if (!factCheck.getTitle().equals(title))
            newTitle = title;

        if (!factCheck.getRating().equals(rating))
            newRating = rating;

        if (!factCheck.getClaim().equals(claim))
            newClaim = claim;

        var contentChanges = textDifferenceProcessor.getDifference(factCheck.getContent(), content);

        if (newTitle == null && newClaim == null && newRating == null && contentChanges.size() == 0)
            throw new BadRequestResponse("No changes compared to current version.");

        var edit = new StandardFactCheckEdit(ObjectId.get(), factCheckId, description, newTitle, newClaim, newRating, contentChanges, user.getId());
        repository.createEdit(edit);
    }

    public void getEditPreviews(@NonNull Context context) {
        ObjectId articleId;
        try {
            articleId = new ObjectId(context.pathParam("factcheckId"));
        } catch (IllegalArgumentException e) {
            throw new BadRequestResponse(e.getMessage());
        }

        var page = context.queryParamAsClass("page", Integer.class).getOrDefault(0);
        var limit = context.queryParamAsClass("limit", Integer.class).getOrDefault(10);

        if (page < 0 || limit < 0 || limit > 10)
            throw new BadRequestResponse("Invalid page or limit.");

        // TODO: Check permission.
        var factCheckPreviews = repository.getEditPreviews(articleId, page, limit);
        context.json(factCheckPreviews);
    }

    public void getEditById(@NonNull Context context) {
        ObjectId factcheckId;
        ObjectId editId;
        try {
            factcheckId = new ObjectId(context.pathParam("factcheckId"));
            editId = new ObjectId(context.pathParam("editId"));
        } catch (IllegalArgumentException e) {
            throw new BadRequestResponse(e.getMessage());
        }

        var factCheck = repository.findById(factcheckId).orElseThrow(() -> new NotFoundResponse("FactCheck not found."));
        var edit = repository.findEditById(editId).orElseThrow(() -> new NotFoundResponse("Edit not found."));

        var editsAfter = repository.getEditDifferencesAfter(factcheckId, editId);

        var newContent = factCheck.getContent();
        for (FactCheckEdit editAfter : editsAfter) {
            newContent = textDifferenceProcessor.applyDifferences(newContent, editAfter.getDifferences());
        }
        var oldContent = textDifferenceProcessor.applyDifferences(newContent, edit.getDifferences());

        var computedEdit = new ComputedFactCheckEdit(edit, oldContent, newContent);
        context.json(computedEdit);
    }
}
