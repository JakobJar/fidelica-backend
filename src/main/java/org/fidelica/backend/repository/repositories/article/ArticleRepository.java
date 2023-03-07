package org.fidelica.backend.repository.repositories.article;

import org.bson.types.ObjectId;
import org.fidelica.backend.article.Article;
import org.fidelica.backend.article.history.ArticleEdit;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository {

    void create(Article article, ArticleEdit firstEdit);

    Optional<Article> findById(ObjectId id);

    Optional<Article> findPreviewById(ObjectId id);

    void createEdit(ArticleEdit edit);

    Optional<ArticleEdit> findEditById(ObjectId id);

    boolean checkEdit(ObjectId id, boolean approve, ObjectId checkerId, String comment);

    List<ArticleEdit> getUncheckedEditPreviews(int limit, int offset);

    List<ArticleEdit> getEditPreviews(ObjectId articleId, int limit, int offset);

    List<ArticleEdit> getEditDifferencesAfter(ObjectId articleId, ObjectId editId);
}
