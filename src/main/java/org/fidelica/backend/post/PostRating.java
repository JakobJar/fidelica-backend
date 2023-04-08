package org.fidelica.backend.post;

import lombok.Getter;

@Getter
public enum PostRating {

    TRUE("True"),
    MOSTLY_TRUE("Mostly true"),
    MIXED("Mixed"),
    MOSTLY_FALSE("Mostly false"),
    FALSE("False");

    private final String displayName;

    PostRating(String displayName) {
        this.displayName = displayName;
    }
}