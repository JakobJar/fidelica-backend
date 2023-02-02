package org.fidelica.backend.factcheck.history;

import lombok.*;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;
import org.fidelica.backend.factcheck.FactCheckRating;
import org.fidelica.backend.factcheck.history.difference.TextDifference;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@BsonDiscriminator("StandardArticleEdit")
public class StandardFactCheckEdit implements FactCheckEdit {

    @BsonId
    private final ObjectId id;
    private final ObjectId factCheckId;
    private final String description;
    private String title;
    private String claim;
    private FactCheckRating rating;
    private final List<TextDifference> differences;
    private final ObjectId editorId;

    private boolean approved;
    private ObjectId checkerId;
    private String comment;

    public StandardFactCheckEdit(ObjectId id, ObjectId factCheckId, String description,
                                 String title, String claim, FactCheckRating rating,
                                 List<TextDifference> differences, ObjectId editorId) {
        this(id, factCheckId, description, title, claim, rating, differences, editorId, false, null, null);
    }

    public StandardFactCheckEdit(@NonNull ObjectId id, @NonNull ObjectId factCheckId, @NonNull String description,
                                 String title, String claim, FactCheckRating rating,
                                 @NonNull List<TextDifference> differences, @NonNull ObjectId editorId,
                                 boolean approved, ObjectId checkerId, String comment) {
        this.id = id;
        this.factCheckId = factCheckId;
        this.description = description;
        this.title = title;
        this.claim = claim;
        this.rating = rating;
        this.differences = differences;
        this.editorId = editorId;
        this.approved = approved;
        this.checkerId = checkerId;
        this.comment = comment;
    }

    @Override
    @BsonIgnore
    public LocalDateTime getDateTime() {
        return id.getDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    @Override
    @BsonIgnore
    public boolean isChecked() {
        return checkerId != null;
    }
}
