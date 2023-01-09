package org.fidelica.backend.article.history.difference;

public interface TextDifference {

    int getStartIndex();

    int getEndIndex();

    String getReplacement();
}
