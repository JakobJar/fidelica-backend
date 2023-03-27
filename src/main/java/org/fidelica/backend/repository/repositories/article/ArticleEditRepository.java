package org.fidelica.backend.repository.repositories.article;

import org.bson.types.ObjectId;
import org.fidelica.backend.article.history.ArticleEdit;
import org.fidelica.backend.article.history.difference.TextDifference;

import java.util.List;
import java.util.Optional;

public interface ArticleEditRepository {

    void create(ArticleEdit edit);

    Optional<ArticleEdit> findById(ObjectId id);

    boolean updateDifferences(ObjectId id, List<TextDifference> differences);

    boolean check(ObjectId id, boolean approve, ObjectId checkerId, String comment);

    boolean isFirst(ObjectId articleId, ObjectId id);

    void disproveOtherEdits(ObjectId articleId, ObjectId editId, ObjectId checkerId);

    List<ArticleEdit> getUncheckedPreviews(int limit, int offset);

    List<ArticleEdit> getPreviews(ObjectId articleId, int limit, int offset);

    List<ArticleEdit> getDifferencesAfter(ObjectId articleId, ObjectId editId);
}
