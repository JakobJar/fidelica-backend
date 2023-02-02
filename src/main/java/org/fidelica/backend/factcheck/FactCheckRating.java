package org.fidelica.backend.factcheck;

import lombok.Getter;

@Getter
public enum FactCheckRating {

    TRUE("True"),
    MOSTLY_TRUE("Mostly true"),
    MIXED("Mixed"),
    MOSTLY_FALSE("Mostly false"),
    FALSE("False");

    private final String displayName;

    FactCheckRating(String displayName) {
        this.displayName = displayName;
    }
}
