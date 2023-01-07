package org.fidelica.backend.article.history;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public final class StandardTextDifference implements TextDifference {
    private final int startIndex;
    private final int endIndex;
    private final String replacement;
}
