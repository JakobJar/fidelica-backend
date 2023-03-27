package org.fidelica.backend.repository.repositories.article;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.TextSearchOptions;
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
import java.util.Locale;
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
    public void create(@NonNull Article article, @NonNull ArticleEdit firstEdit) {
        articles.insertOne(article);
        edits.insertOne(firstEdit);
    }

    @Override
    public Optional<Article> findById(@NonNull ObjectId id) {
        return Optional.ofNullable(articles.find(eq("_id", id)).first());
    }

    @Override
    public Optional<Article> findPreviewById(@NonNull ObjectId id) {
        return Optional.ofNullable(articles.find(eq("_id", id))
                .projection(Projections.exclude("content"))
                .first());
    }

    @Override
    public List<Article> search(@NonNull String query, @NonNull Locale locale) {
        var searchOptions = new TextSearchOptions()
                .caseSensitive(false)
                .language(locale.getLanguage());
        return articles.find(text(query, searchOptions))
                .projection(Projections.exclude("content"))
                .sort(Sorts.metaTextScore("score"))
                .into(new ArrayList<>());
    }

    @Override
    public void updateVisibility(@NonNull ObjectId id, boolean visible) {
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
    public void createEdit(@NonNull ArticleEdit edit) {
        edits.insertOne(edit);
    }

    @Override
    public Optional<ArticleEdit> findEditById(@NonNull ObjectId id) {
        return Optional.ofNullable(edits.find(eq("_id", id)).first());
    }

    @Override
    public boolean updateEditDifferences(@NonNull ObjectId id, @NonNull List<TextDifference> differences) {
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
    public boolean checkEdit(@NonNull ObjectId id, boolean approve, @NonNull ObjectId checkerId, @NonNull String comment) {
        var changes = Updates.combine(Updates.set("approve", approve),
                Updates.set("checkerId", checkerId),
                Updates.set("comment", comment));
        return edits.updateOne(and(eq("_id", id), eq("checkerId", null)), changes)
                .wasAcknowledged();
    }

    @Override
    public boolean isFirstEdit(@NonNull ObjectId articleId, @NonNull ObjectId id) {
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
    public List<ArticleEdit> getEditPreviews(@NonNull ObjectId articleId, int limit, int offset) {
        return edits.find(eq("articleId", articleId))
                .projection(EDIT_PREVIEW_PROJECTION)
                .skip(offset * limit)
                .limit(limit)
                .into(new ArrayList<>());
    }

    @Override
    public List<ArticleEdit> getEditDifferencesAfter(@NonNull ObjectId articleId, @NonNull ObjectId editId) {
        return edits.find((and(eq("articleId", articleId), gt("_id", editId))))
                .sort(Sorts.descending("_id"))
                .projection(Projections.include("differences"))
                .into(new ArrayList<>());
    }
}
