package org.fidelica.backend.repository.article;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.fidelica.backend.article.Article;
import org.fidelica.backend.article.history.ArticleEdit;
import org.fidelica.backend.article.history.StandardArticleEdit;
import org.fidelica.backend.user.User;

import java.util.Collections;
import java.util.Optional;

public class StandardArticleRepository implements ArticleRepository {

    private final MongoCollection<Article> articles;
    private final MongoCollection<ArticleEdit> edits;

    public StandardArticleRepository(@NonNull MongoDatabase database) {
        this.articles = database.getCollection("articles", Article.class);
        this.edits = database.getCollection("edits", ArticleEdit.class);
    }

    @Override
    public void create(Article article, User creator) {
        articles.insertOne(article);

        var firstEdit = new StandardArticleEdit(ObjectId.get(), article.getId(),
                "Creation", Collections.emptyList(), creator.getId());
        edits.insertOne(firstEdit);
    }

    @Override
    public Optional<Article> findById(ObjectId id) {
        return Optional.empty();
    }
}
