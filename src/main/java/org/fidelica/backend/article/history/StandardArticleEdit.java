package org.fidelica.backend.article.history;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.bson.codecs.pojo.annotations.*;
import org.bson.types.ObjectId;
import org.fidelica.backend.article.history.difference.TextDifference;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Data
@EqualsAndHashCode(of = "id")
@BsonDiscriminator("StandardArticleEdit")
public class StandardArticleEdit implements ArticleEdit {

    @BsonId
    private final ObjectId id;
    private final ObjectId articleId;
    private final String description;
    private final List<TextDifference> differences;
    private final ObjectId editorId;

    private boolean approved;
    private ObjectId checkerId;
    private String comment;

    public StandardArticleEdit(ObjectId id, ObjectId articleId, String description, List<TextDifference> differences, ObjectId editorId) {
        this(id, articleId, differences, description, editorId, false, null, null);
    }

    @BsonCreator
    public StandardArticleEdit(@NonNull @BsonId ObjectId id, @NonNull @BsonProperty("articleId") ObjectId articleId,
                               @NonNull @BsonProperty("differences") List<TextDifference> differences,
                               @NonNull @BsonProperty("description") String description,
                               @NonNull @BsonProperty("editorId") ObjectId editorId,
                               @BsonProperty("approved") boolean approved, @BsonProperty("checkerId") ObjectId checkerId,
                               @BsonProperty("comment") String comment) {
        this.id = id;
        this.articleId = articleId;
        this.differences = differences;
        this.description = description;
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
