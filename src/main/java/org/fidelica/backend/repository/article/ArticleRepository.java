package org.fidelica.backend.repository.article;

import org.bson.types.ObjectId;
import org.fidelica.backend.article.Article;
import org.fidelica.backend.article.history.ArticleEdit;

import java.util.Optional;

public interface ArticleRepository {

    void create(Article article, ArticleEdit firstEdit);

    Optional<Article> findById(ObjectId id);

}
