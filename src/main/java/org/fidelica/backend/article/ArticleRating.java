package org.fidelica.backend.article;

import lombok.Getter;

@Getter
public enum ArticleRating {

    TRUE("True"),
    MOSTLY_TRUE("Mostly true"),
    MIXED("Mixed"),
    MOSTLY_FALSE("Mostly false"),
    FALSE("False");

    private final String displayName;

    ArticleRating(String displayName) {
        this.displayName = displayName;
    }
}
