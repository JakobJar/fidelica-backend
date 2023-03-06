package org.fidelica.backend.rest.routes.factcheck;

import com.google.inject.Inject;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.fidelica.backend.factcheck.history.ComputedFactCheckEdit;
import org.fidelica.backend.factcheck.history.FactCheckEdit;
import org.fidelica.backend.factcheck.history.difference.TextDifferenceProcessor;
import org.fidelica.backend.repository.repositories.article.FactCheckRepository;


public class FactCheckEditController {

    private final FactCheckRepository repository;
    private final TextDifferenceProcessor textDifferenceProcessor;

    @Inject
    public FactCheckEditController(@NonNull FactCheckRepository repository, @NonNull TextDifferenceProcessor textDifferenceProcessor) {
        this.repository = repository;
        this.textDifferenceProcessor = textDifferenceProcessor;
    }

    public void getFactCheckEditPreviews(@NonNull Context context) {
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
