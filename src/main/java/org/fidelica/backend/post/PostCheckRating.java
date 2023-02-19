package org.fidelica.backend.post;

import lombok.Getter;

@Getter
public enum PostCheckRating {

    TRUE("True"),
    MOSTLY_TRUE("Mostly true"),
    MIXED("Mixed"),
    MOSTLY_FALSE("Mostly false"),
    FALSE("False");

    private final String displayName;

    PostCheckRating(String displayName) {
        this.displayName = displayName;
    }
}