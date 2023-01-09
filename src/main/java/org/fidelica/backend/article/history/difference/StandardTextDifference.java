package org.fidelica.backend.article.history.difference;

import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public final class StandardTextDifference implements TextDifference {
    private final int startIndex;
    private final int endIndex;
    private final String replacement;

    public StandardTextDifference(int startIndex, int endIndex, String replacement) {
        Preconditions.checkPositionIndex(startIndex, endIndex);

        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.replacement = replacement;
    }
}
