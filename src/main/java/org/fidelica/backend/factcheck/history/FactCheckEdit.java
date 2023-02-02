package org.fidelica.backend.factcheck.history;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.types.ObjectId;
import org.fidelica.backend.factcheck.FactCheckRating;
import org.fidelica.backend.factcheck.history.difference.TextDifference;
import org.fidelica.backend.repository.Identifiable;

import java.time.LocalDateTime;
import java.util.List;

@BsonDiscriminator
public interface FactCheckEdit extends Identifiable {

    ObjectId getFactCheckId();

    String getClaim();

    FactCheckRating getRating();

    List<TextDifference> getDifferences();

    ObjectId getEditorId();

    LocalDateTime getDateTime();

    boolean isApproved();

    boolean isChecked();

    ObjectId getCheckerId();

    String getComment();

}
