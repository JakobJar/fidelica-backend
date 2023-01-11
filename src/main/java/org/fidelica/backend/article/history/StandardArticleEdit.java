package org.fidelica.backend.article.history;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
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
    private final List<TextDifference> differences;
    private final ObjectId editorId;

    public StandardArticleEdit(@NonNull ObjectId id, @NonNull ObjectId articleId,
                               @NonNull List<TextDifference> differences, @NonNull ObjectId editorId) {
        this.id = id;
        this.articleId = articleId;
        this.differences = differences;
        this.editorId = editorId;
    }

    @Override
    @BsonIgnore
    public LocalDateTime getDateTime() {
        return id.getDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
