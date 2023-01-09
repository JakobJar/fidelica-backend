package org.fidelica.backend.article;

import org.fidelica.backend.repository.Identifiable;

public interface Article extends Identifiable {

    String getTitle();

    String getShortDescription();

    String getContent();
}
