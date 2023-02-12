package org.fidelica.backend.repository.article;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import lombok.NonNull;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.fidelica.backend.factcheck.FactCheck;
import org.fidelica.backend.factcheck.history.FactCheckEdit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.*;

public class StandardFactCheckRepository implements FactCheckRepository {

    private static final Bson EDIT_PREVIEW_PROJECTION = Projections.include("_t", "_id", "factCheckId",
            "description", "editorId", "approved", "checkerId", "comment");

    private final MongoCollection<FactCheck> articles;
    private final MongoCollection<FactCheckEdit> edits;

    @Inject
    public StandardFactCheckRepository(@NonNull MongoDatabase database) {
        this.articles = database.getCollection("articles", FactCheck.class);
        this.edits = database.getCollection("article_edits", FactCheckEdit.class);
    }

    @Override
    public void create(FactCheck factCheck, FactCheckEdit firstEdit) {
        articles.insertOne(factCheck);
        edits.insertOne(firstEdit);
    }

    @Override
    public Optional<FactCheck> findById(ObjectId id) {
        return Optional.ofNullable(articles.find(eq("_id", id)).first());
    }

    @Override
    public Optional<FactCheckEdit> findEditById(ObjectId id) {
        return Optional.ofNullable(edits.find(eq("_id", id)).first());
    }

    @Override
    public List<FactCheckEdit> getEditPreviews(ObjectId factcheckId, int limit, int offset) {
        return edits.find(eq("factCheckId", factcheckId))
                .projection(EDIT_PREVIEW_PROJECTION)
                .skip(offset * limit)
                .limit(limit)
                .into(new ArrayList<>());
    }

    @Override
    public List<FactCheckEdit> getEditDifferencesAfter(ObjectId factcheckId, ObjectId editId) {
        return edits.find((and(eq("factCheckId", factcheckId), gt("_id", editId))))
                .sort(Sorts.descending("_id"))
                .projection(Projections.include("differences"))
                .into(new ArrayList<>());
    }
}
