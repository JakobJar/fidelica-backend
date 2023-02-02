package org.fidelica.backend.factcheck.history.difference;

import com.google.common.base.Preconditions;
import lombok.Data;
import lombok.NonNull;

@Data
public final class StandardTextDifference implements TextDifference {
    private final int startIndex;
    private final int endIndex;
    private final String replacement;

    public StandardTextDifference(int startIndex, int endIndex, @NonNull String replacement) {
        Preconditions.checkPositionIndex(startIndex, endIndex);

        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.replacement = replacement;
    }
}
