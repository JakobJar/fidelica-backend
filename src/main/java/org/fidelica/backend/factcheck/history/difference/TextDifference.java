package org.fidelica.backend.factcheck.history.difference;

public interface TextDifference {

    int getStartIndex();

    int getEndIndex();

    String getReplacement();
}
