package org.fidelica.backend.post.history;

import lombok.*;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;
import org.fidelica.backend.article.ArticleRating;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@BsonDiscriminator("StandardCheckEdit")
public class StandardCheckEdit implements CheckEdit {

    @BsonId
    private final ObjectId id;
    private final ObjectId postId;
    private String note;
    private ArticleRating rating;
    private final ObjectId editorId;

    private boolean approved;
    private ObjectId checkerId;
    private String comment;

    public StandardCheckEdit(ObjectId id, ObjectId postId, String note, ArticleRating rating, ObjectId editorId) {
        this(id, postId, note, rating, editorId, false, null, null);
    }

    public StandardCheckEdit(@NonNull ObjectId id, @NonNull ObjectId postId, String note, ArticleRating rating,
                             @NonNull ObjectId editorId, boolean approved, ObjectId checkerId, String comment) {
        this.id = id;
        this.postId = postId;
        this.note = note;
        this.rating = rating;
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
