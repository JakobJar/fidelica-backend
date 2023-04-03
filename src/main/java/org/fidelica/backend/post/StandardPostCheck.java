package org.fidelica.backend.post;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.LinkedHashSet;

@Data
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@BsonDiscriminator("StandardPostCheck")
public class StandardPostCheck implements PostCheck {

    private PostCheckRating rating;
    private String note;
    private final Collection<ObjectId> relatedArticles;

    private boolean visible;
    private boolean editable;

    public StandardPostCheck(PostCheckRating rating, String comment) {
        this(rating, comment, new LinkedHashSet<>(), false, true);
    }

    public StandardPostCheck(@NonNull PostCheckRating rating, String note, @NonNull Collection<ObjectId> relatedArticles,
                             boolean visible, boolean editable) {
        this.rating = rating;
        this.note = note;
        this.relatedArticles = relatedArticles;
        this.visible = visible;
        this.editable = editable;
    }
}
