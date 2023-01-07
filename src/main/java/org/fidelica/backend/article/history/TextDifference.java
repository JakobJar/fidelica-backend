package org.fidelica.backend.article.history;

public interface TextDifference {

    int getStartIndex();

    int getEndIndex();

    String getReplacement();
}
