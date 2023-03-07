package org.fidelica.backend.repository.repositories.article;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import lombok.NonNull;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.fidelica.backend.article.Article;
import org.fidelica.backend.article.history.ArticleEdit;

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
    public void createEdit(ArticleEdit edit) {
        edits.insertOne(edit);
    }

    @Override
    public Optional<ArticleEdit> findEditById(ObjectId id) {
        return Optional.ofNullable(edits.find(eq("_id", id)).first());
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
        var changes = new Document();
        changes.append("approved", approve);
        changes.append("checkerId", checkerId);
        changes.append("comment", comment);

        // TODO: Remove other pending edits

        return edits.updateOne(and(eq("_id", id), eq("checkerId", null)),
                changes).wasAcknowledged();
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
