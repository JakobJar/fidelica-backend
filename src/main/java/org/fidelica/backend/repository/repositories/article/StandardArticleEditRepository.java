package org.fidelica.backend.repository.repositories.article;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import lombok.NonNull;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.fidelica.backend.article.history.ArticleEdit;
import org.fidelica.backend.article.history.difference.TextDifference;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.*;

public class StandardArticleEditRepository implements ArticleEditRepository {

    private static final Bson PREVIEW_PROJECTION = Projections.include("_t", "_id", "articleId",
            "description", "editorId", "approved", "checkerId", "comment");

    private final MongoCollection<ArticleEdit> edits;

    @Inject
    public StandardArticleEditRepository(@NonNull MongoDatabase database) {
        this.edits = database.getCollection("article_edits", ArticleEdit.class);
    }

    @Override
    public void create(@NonNull ArticleEdit edit) {
        edits.insertOne(edit);
    }

    @Override
    public Optional<ArticleEdit> findById(@NonNull ObjectId id) {
        return Optional.ofNullable(edits.find(eq("_id", id)).first());
    }

    @Override
    public boolean updateDifferences(@NonNull ObjectId id, @NonNull List<TextDifference> differences) {
        var changes = Updates.set("differences", differences);
        return edits.updateOne(eq("_id", id), changes).wasAcknowledged();
    }

    @Override
    public List<ArticleEdit> getUncheckedPreviews(int limit, int offset) {
        return edits.find(eq("checkerId", null))
                .projection(PREVIEW_PROJECTION)
                .skip(offset * limit)
                .limit(limit)
                .into(new ArrayList<>());
    }

    @Override
    public boolean check(@NonNull ObjectId id, boolean approve, @NonNull ObjectId checkerId, @NonNull String comment) {
        var changes = Updates.combine(Updates.set("approve", approve),
                Updates.set("checkerId", checkerId),
                Updates.set("comment", comment));
        return edits.updateOne(and(eq("_id", id), eq("checkerId", null)), changes)
                .wasAcknowledged();
    }

    @Override
    public boolean isFirst(@NonNull ObjectId articleId, @NonNull ObjectId id) {
        var result = edits.find(eq("articleId", articleId))
                .projection(Projections.include("_id"))
                .into(new ArrayList<>());
        return result.size() == 1 && result.get(0).getId().equals(id);
    }

    @Override
    public void disproveOtherEdits(@NonNull ObjectId articleId, @NonNull ObjectId editId, @NonNull ObjectId checkerId) {
        var changes = Updates.combine(
                Updates.set("approve", false),
                Updates.set("checkerId", checkerId),
                Updates.set("comment", "Changes were overwritten by edit " + editId));
        edits.updateMany(and(eq("articleId", articleId), ne("_id", editId)),
                changes);
    }

    @Override
    public List<ArticleEdit> getPreviews(@NonNull ObjectId articleId, int limit, int offset) {
        return edits.find(eq("articleId", articleId))
                .projection(PREVIEW_PROJECTION)
                .skip(offset * limit)
                .limit(limit)
                .into(new ArrayList<>());
    }

    @Override
    public List<ArticleEdit> getDifferencesAfter(@NonNull ObjectId articleId, @NonNull ObjectId editId) {
        return edits.find((and(eq("articleId", articleId), gt("_id", editId))))
                .sort(Sorts.descending("_id"))
                .projection(Projections.include("differences"))
                .into(new ArrayList<>());
    }
}
