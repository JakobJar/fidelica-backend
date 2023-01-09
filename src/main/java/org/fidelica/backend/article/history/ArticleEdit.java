package org.fidelica.backend.article.history;

import org.bson.types.ObjectId;
import org.fidelica.backend.article.history.difference.TextDifference;
import org.fidelica.backend.repository.Identifiable;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleEdit extends Identifiable {

    ObjectId getArticleId();

    List<TextDifference> getDifferences();

    ObjectId getEditorId();

    LocalDateTime getDateTime();
}
