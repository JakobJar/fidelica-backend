package org.fidelica.backend.repository.article;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.fidelica.backend.article.Article;
import org.fidelica.backend.article.history.ArticleEdit;

import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public class StandardArticleRepository implements ArticleRepository {

    private final MongoCollection<Article> articles;
    private final MongoCollection<ArticleEdit> edits;

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
}
