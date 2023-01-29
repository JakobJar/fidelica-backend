package org.fidelica.backend.article;

import org.fidelica.backend.repository.Identifiable;

import java.util.Locale;

public interface Article extends Identifiable {

    String getTitle();

    String getShortDescription();

    String getContent();

    Locale getLanguage();
}
