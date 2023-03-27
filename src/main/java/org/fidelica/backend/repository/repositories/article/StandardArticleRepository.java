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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.text;

public class StandardArticleRepository implements ArticleRepository {

    private static final Bson PREVIEW_PROJECTION = Projections.exclude("content");

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
                .projection(PREVIEW_PROJECTION)
                .first());
    }

    @Override
    public List<Article> search(@NonNull String query, @NonNull Locale locale) {
        var searchOptions = new TextSearchOptions()
                .caseSensitive(false)
                .language(locale.getLanguage());
        return articles.find(text(query, searchOptions))
                .projection(PREVIEW_PROJECTION)
                .sort(Sorts.metaTextScore("score"))
                .limit(5)
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
}
