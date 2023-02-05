package org.fidelica.backend.factcheck;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.fidelica.backend.repository.Identifiable;

import java.util.Locale;

@BsonDiscriminator
public interface FactCheck extends Identifiable {

    String getTitle();

    String getClaim();

    FactCheckRating getRating();

    String getContent();

    Locale getLanguage();

    boolean isVisible();

    boolean isEditable();
}
