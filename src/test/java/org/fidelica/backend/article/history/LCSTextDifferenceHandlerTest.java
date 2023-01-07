package org.fidelica.backend.article.history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class LCSTextDifferenceHandlerTest {

    private LCSTextDifferenceHandler differenceHandler;

    @BeforeEach
    void setup() {
        differenceHandler = new LCSTextDifferenceHandler();
    }

    @Nested
    class Difference {

        @Test
        void testDifference() {
            String text1 = "This is a sample text. It has several sentences. Some words have been changed.";
            String text2 = "This text is a example. It has several sentences. A few words were modified.";

            var result = differenceHandler.getDifference(text1, text2);
            assertThat(result).containsExactly(new StandardTextDifference(5, 8, "text"),
                    new StandardTextDifference(10, 14, "is a "),
                    new StandardTextDifference(16, 53, "xample. It has several sentences. A fe"),
                    new StandardTextDifference(55, 62, " words w"),
                    new StandardTextDifference(64, 75, "re modified."),
                    new StandardTextDifference(76, 77, ""));
        }

        @Test
        void testDifferenceWithEmpty() {
            var result1 = differenceHandler.getDifference("abc", "");
            assertThat(result1).containsExactly(new StandardTextDifference(0, 2, ""));

            var result2 = differenceHandler.getDifference("", "abc");
            assertThat(result2).containsExactly(new StandardTextDifference(0, 2, "abc"));
        }

        @Test
        void testDifferenceSingleWord() {
            //Edit end
            var result1 = differenceHandler.getDifference("abc", "aba");
            assertThat(result1).containsExactly(new StandardTextDifference(2, 2, "a"));

            //Edit begin
            var result2 = differenceHandler.getDifference("abc", "cbc");
            assertThat(result2).containsExactly(new StandardTextDifference(0, 0, "c"));

            //Add
            var result3 = differenceHandler.getDifference("abc", "abcd");
            assertThat(result3).containsExactly(new StandardTextDifference(3, 3, "d"));

            //Remove
            var result4 = differenceHandler.getDifference("abcd", "abc");
            assertThat(result4).containsExactly(new StandardTextDifference(3, 3, ""));
        }
    }

    @Nested
    class Application {

        @Test
        void testApply() {
            String text1 = "This is a sample text. It has several sentences. Some words have been changed.";
            String text2 = "This text is a example. It has several sentences. A few words were modified.";

            var diffs = differenceHandler.getDifference(text1, text2);
            var result = differenceHandler.applyDifferences(text1, diffs);

            assertThat(result).isEqualTo(text2);
        }
    }
}