package org.fidelica.backend.article.history.difference;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class LCSTextDifferenceProcessor implements TextDifferenceProcessor {

    @Override
    public List<TextDifference> getDifference(String original, String edited) {
        Preconditions.checkNotNull(original);
        if (edited == null)
            return Collections.emptyList();

        List<TextDifference> differences = new ArrayList<>();

        // split the strings into arrays of characters
        char[] originalChars = original.toCharArray();
        char[] editedChars = edited.toCharArray();

        // get the length of the shorter array
        int shorterLength = Math.min(originalChars.length, editedChars.length);

        // find the differences between the two arrays
        int i = 0;
        while (i < shorterLength) {
            if (originalChars[i] != editedChars[i]) {
                int start = i;
                int end = i;
                while (end < shorterLength - 1 && originalChars[end + 1] != editedChars[end + 1]) {
                    end++;
                }
                String replacement = new String(editedChars, start, end - start + 1);
                differences.add(new StandardTextDifference(start, end, replacement));
                i = end;
            }
            i++;
        }

        // if one array is longer than the other, add an additional change for the remaining characters
        if (originalChars.length > shorterLength) {
            differences.add(new StandardTextDifference(shorterLength, originalChars.length - 1, ""));
        } else if (editedChars.length > shorterLength) {
            String replacement = new String(editedChars, shorterLength, editedChars.length - shorterLength);
            differences.add(new StandardTextDifference(shorterLength, editedChars.length - 1, replacement));
        }

        return differences;
    }

    @Override
    public String applyDifferences(String input, Collection<TextDifference> differences) {
        Preconditions.checkNotNull(input, "input must be not null");
        StringBuilder builder = new StringBuilder(input);
        for (TextDifference difference : differences) {
            builder.replace(difference.getStartIndex(), difference.getEndIndex() + 1, difference.getReplacement());
        }
        return builder.toString();
    }
}
