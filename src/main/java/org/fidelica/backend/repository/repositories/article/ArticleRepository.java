package org.fidelica.backend.repository.repositories.article;

import org.bson.types.ObjectId;
import org.fidelica.backend.article.Article;
import org.fidelica.backend.article.history.ArticleEdit;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface ArticleRepository {

    void create(Article article, ArticleEdit firstEdit);

    Optional<Article> findById(ObjectId id);

    Optional<Article> findPreviewById(ObjectId id);

    List<Article> search(String query, Locale locale);

    void updateVisibility(ObjectId id, boolean visible);

    boolean update(ObjectId id, String title, String shortDescription, String content);
}
