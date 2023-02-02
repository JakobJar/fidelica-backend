package org.fidelica.backend.factcheck;

import lombok.*;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.Locale;

@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@BsonDiscriminator("StandardFactCheck")
public class StandardFactCheck implements FactCheck {

    @BsonId
    private final ObjectId id;
    private String title;
    private String claim;
    private FactCheckRating rating;
    private String content;
    private final Locale language;

    private boolean visible;
    private boolean editable;

    public StandardFactCheck(ObjectId id, String title, String claim, FactCheckRating rating, String content, Locale language) {
        this(id, title, claim, rating, content, language, false, true);
    }

    public StandardFactCheck(@NonNull ObjectId id, @NonNull String title, @NonNull String claim,
                             @NonNull FactCheckRating rating, @NonNull String content,
                             @NonNull Locale language, boolean visible, boolean editable) {
        this.id = id;
        this.title = title;
        this.claim = claim;
        this.rating = rating;
        this.content = content;
        this.language = language;
        this.visible = visible;
        this.editable = editable;
    }
}
