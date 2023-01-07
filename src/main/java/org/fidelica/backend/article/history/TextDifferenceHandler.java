package org.fidelica.backend.article.history;

import java.util.Collection;
import java.util.List;

public interface TextDifferenceHandler {

    List<TextDifference> getDifference(String original, String edited);

    String applyDifferences(String input, Collection<TextDifference> differences);
}
