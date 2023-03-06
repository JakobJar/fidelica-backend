package org.fidelica.backend.article.history;

import lombok.*;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;
import org.fidelica.backend.article.ArticleRating;
import org.fidelica.backend.article.history.difference.TextDifference;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@BsonDiscriminator("StandardArticleEdit")
public class StandardArticleEdit implements ArticleEdit {

    @BsonId
    private final ObjectId id;
    private final ObjectId articleId;
    private final String description;
    private String title;
    private String claim;
    private ArticleRating rating;
    private final List<TextDifference> differences;
    private final ObjectId editorId;

    private boolean approved;
    private ObjectId checkerId;
    private String comment;

    public StandardArticleEdit(ObjectId id, ObjectId articleId, String description,
                               String title, String claim, ArticleRating rating,
                               List<TextDifference> differences, ObjectId editorId) {
        this(id, articleId, description, title, claim, rating, differences, editorId, false, null, null);
    }

    public StandardArticleEdit(@NonNull ObjectId id, @NonNull ObjectId articleId, @NonNull String description,
                               String title, String claim, ArticleRating rating,
                               @NonNull List<TextDifference> differences, @NonNull ObjectId editorId,
                               boolean approved, ObjectId checkerId, String comment) {
        this.id = id;
        this.articleId = articleId;
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
