package org.fidelica.backend;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.json.JsonWriter;
import org.fidelica.backend.article.history.LCSTextDifferenceHandler;
import org.fidelica.backend.article.history.TextDifference;
import org.fidelica.backend.article.history.TextDifferenceHandler;

import java.util.List;

public class FidelicaBackend {

    public static void main(String[] args) {
        String text1 = "This is a sample text. It has several sentences. Some words have been changed.";
        String text2 = "This text is a example. It has several sentences. A few words were modified.";


        TextDifferenceHandler handler = new LCSTextDifferenceHandler();
        List<TextDifference> diffs = handler.getDifference(text1, text2);

        StringBuilder replaced = new StringBuilder(text1);
        for (TextDifference diff : diffs) {
            System.out.println(diff);
            replaced.replace(diff.getStartIndex(), diff.getEndIndex() + 1, diff.getReplacement());
        }
        System.out.println(replaced.toString());
// diffs: [Diff(startIndex=4, endIndex=5, addedText=" ", removedText=""),
//         Diff(startIndex=9, endIndex=10, addedText=" ", removedText=""),
//         Diff(startIndex=11, endIndex=15, addedText="example", removedText="sample"),
//         Diff(startIndex=26, endIndex=29, addedText="few", removedText="some"),
//         Diff(startIndex=35, endIndex=38, addedText="mod", removedText="chang")]
    }
}
