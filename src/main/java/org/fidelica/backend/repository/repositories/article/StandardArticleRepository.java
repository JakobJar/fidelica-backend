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
import org.fidelica.backend.article.Article;
import org.fidelica.backend.article.ArticleRating;
import org.fidelica.backend.article.history.ArticleEdit;
import org.fidelica.backend.article.history.difference.TextDifference;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.*;

public class StandardArticleRepository implements ArticleRepository {

    private static final Bson EDIT_PREVIEW_PROJECTION = Projections.include("_t", "_id", "articleId",
            "description", "editorId", "approved", "checkerId", "comment");

    private final MongoCollection<Article> articles;
    private final MongoCollection<ArticleEdit> edits;

    @Inject
    public StandardArticleRepository(@NonNull MongoDatabase database) {
        this.articles = database.getCollection("articles", Article.class);
        this.edits = database.getCollection("article_edits", ArticleEdit.class);
    }

    @Override
    public void create(Article article, ArticleEdit firstEdit) {
        articles.insertOne(article);
        edits.insertOne(firstEdit);
    }

    @Override
    public Optional<Article> findById(ObjectId id) {
        return Optional.ofNullable(articles.find(eq("_id", id)).first());
    }

    @Override
    public Optional<Article> findPreviewById(ObjectId id) {
        return Optional.ofNullable(articles.find(eq("_id", id))
                .projection(Projections.exclude("content"))
                .first());
    }

    @Override
    public void updateVisibility(ObjectId id, boolean visible) {
        articles.updateOne(eq("_id", id),
                Updates.set("visible", visible));
    }

    @Override
    public boolean update(@NonNull ObjectId id, String title, String shortDescription, String content, ArticleRating rating) {
        List<Bson> changes = new ArrayList<>();
        if (title != null)
            changes.add(Updates.set("title", title));
        if (shortDescription != null)
            changes.add(Updates.set("shortDescription", shortDescription));
        if (content != null)
            changes.add(Updates.set("content", content));
        if (rating != null)
            changes.add(Updates.set("rating", rating));

        return articles.updateOne(eq("_id", id), Updates.combine(changes)).wasAcknowledged();
    }

    @Override
    public void createEdit(ArticleEdit edit) {
        edits.insertOne(edit);
    }

    @Override
    public Optional<ArticleEdit> findEditById(ObjectId id) {
        return Optional.ofNullable(edits.find(eq("_id", id)).first());
    }

    @Override
    public boolean updateEditDifferences(ObjectId id, List<TextDifference> differences) {
        var changes = Updates.set("differences", differences);
        return edits.updateOne(eq("_id", id), changes).wasAcknowledged();
    }

    @Override
    public List<ArticleEdit> getUncheckedEditPreviews(int limit, int offset) {
        return edits.find(eq("checkerId", null))
                .projection(EDIT_PREVIEW_PROJECTION)
                .skip(offset * limit)
                .limit(limit)
                .into(new ArrayList<>());
    }

    @Override
    public boolean checkEdit(ObjectId id, boolean approve, ObjectId checkerId, String comment) {
        var changes = Updates.combine(Updates.set("approve", approve),
                Updates.set("checkerId", checkerId),
                Updates.set("comment", comment));
        return edits.updateOne(and(eq("_id", id), eq("checkerId", null)), changes)
                .wasAcknowledged();
    }

    @Override
    public boolean isFirstEdit(ObjectId edit, ObjectId id) {
        return false;
    }

    @Override
    public void disproveOtherEdits(ObjectId articleId, ObjectId editId, ObjectId checkerId) {
        var changes = Updates.combine(
                Updates.set("approve", false),
                Updates.set("checkerId", checkerId),
                Updates.set("comment", "Changes were overwritten by edit " + editId));
        edits.updateMany(and(eq("articleId", articleId), ne("_id", editId)),
                changes);
    }

    @Override
    public List<ArticleEdit> getEditPreviews(ObjectId articleId, int limit, int offset) {
        return edits.find(eq("articleId", articleId))
                .projection(EDIT_PREVIEW_PROJECTION)
                .skip(offset * limit)
                .limit(limit)
                .into(new ArrayList<>());
    }

    @Override
    public List<ArticleEdit> getEditDifferencesAfter(ObjectId articleId, ObjectId editId) {
        return edits.find((and(eq("articleId", articleId), gt("_id", editId))))
                .sort(Sorts.descending("_id"))
                .projection(Projections.include("differences"))
                .into(new ArrayList<>());
    }
}
