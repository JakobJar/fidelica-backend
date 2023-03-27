package org.fidelica.backend.repository.repositories.article;

import org.bson.types.ObjectId;
import org.fidelica.backend.article.Article;
import org.fidelica.backend.article.ArticleRating;
import org.fidelica.backend.article.history.ArticleEdit;
import org.fidelica.backend.article.history.difference.TextDifference;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface ArticleRepository {

    void create(Article article, ArticleEdit firstEdit);

    Optional<Article> findById(ObjectId id);

    Optional<Article> findPreviewById(ObjectId id);

    List<Article> search(String query, Locale locale);

    void updateVisibility(ObjectId id, boolean visible);

    boolean update(ObjectId id, String title, String shortDescription, String content, ArticleRating rating);

    void createEdit(ArticleEdit edit);

    Optional<ArticleEdit> findEditById(ObjectId id);

    boolean updateEditDifferences(ObjectId id, List<TextDifference> differences);

    boolean checkEdit(ObjectId id, boolean approve, ObjectId checkerId, String comment);

    boolean isFirstEdit(ObjectId articleId, ObjectId id);

    void disproveOtherEdits(ObjectId articleId, ObjectId editId, ObjectId checkerId);

    List<ArticleEdit> getUncheckedEditPreviews(int limit, int offset);

    List<ArticleEdit> getEditPreviews(ObjectId articleId, int limit, int offset);

    List<ArticleEdit> getEditDifferencesAfter(ObjectId articleId, ObjectId editId);
}
