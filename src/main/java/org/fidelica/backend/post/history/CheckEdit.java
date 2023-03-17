package org.fidelica.backend.post.history;

import org.bson.types.ObjectId;
import org.fidelica.backend.article.ArticleRating;
import org.fidelica.backend.repository.Identifiable;

import java.time.LocalDateTime;

public interface CheckEdit extends Identifiable {

    ObjectId getPostId();

    String getNote();

    ArticleRating getRating();

    ObjectId getEditorId();

    LocalDateTime getDateTime();

    boolean isApproved();

    boolean isChecked();

    ObjectId getCheckerId();

    String getComment();
}
