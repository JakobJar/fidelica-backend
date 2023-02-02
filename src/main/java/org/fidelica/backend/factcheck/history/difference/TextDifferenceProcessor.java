package org.fidelica.backend.factcheck.history.difference;

import java.util.Collection;
import java.util.List;

public interface TextDifferenceProcessor {

    List<TextDifference> getDifference(String original, String edited);

    String applyDifferences(String input, Collection<TextDifference> differences);
}
