package org.fidelica.backend.article.history;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.types.ObjectId;
import org.fidelica.backend.article.history.difference.TextDifference;
import org.fidelica.backend.repository.Identifiable;

import java.time.LocalDateTime;
import java.util.List;

@BsonDiscriminator
public interface ArticleEdit extends Identifiable {

    ObjectId getArticleId();

    String getTitle();

    String getShortDescription();

    List<TextDifference> getDifferences();

    ObjectId getEditorId();

    LocalDateTime getDateTime();

    boolean isApproved();

    boolean isChecked();

    ObjectId getCheckerId();

    String getComment();

}
