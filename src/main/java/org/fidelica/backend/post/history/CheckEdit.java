package org.fidelica.backend.post.history;

import org.bson.types.ObjectId;
import org.fidelica.backend.post.PostCheckRating;
import org.fidelica.backend.repository.Identifiable;

import java.time.LocalDateTime;
import java.util.Collection;

public interface CheckEdit extends Identifiable {

    ObjectId getPostId();

    String getNote();

    PostCheckRating getRating();

    Collection<ObjectId> getRelatedArticles();

    ObjectId getEditorId();

    LocalDateTime getDateTime();

    boolean isApproved();

    boolean isChecked();

    ObjectId getCheckerId();

    String getComment();
}
