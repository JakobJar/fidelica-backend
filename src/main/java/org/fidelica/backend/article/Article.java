package org.fidelica.backend.article;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.fidelica.backend.repository.Identifiable;

import java.util.Locale;

@BsonDiscriminator
public interface Article extends Identifiable {

    String getTitle();

    String getShortDescription();

    String getContent();

    Locale getLanguage();

    boolean isVisible();

    boolean isEditable();
}
