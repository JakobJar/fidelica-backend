package org.fidelica.backend.repository.article;

import org.bson.types.ObjectId;
import org.fidelica.backend.article.Article;
import org.fidelica.backend.user.User;

import java.util.Optional;

public interface ArticleRepository {

    void create(Article firstEdit, User creator);

    Optional<Article> findById(ObjectId id);

}
