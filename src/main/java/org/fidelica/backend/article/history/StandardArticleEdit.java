package org.fidelica.backend.article.history;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import org.fidelica.backend.article.history.difference.TextDifference;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Getter
public class StandardArticleEdit implements ArticleEdit {

    @BsonId
    private final ObjectId id;
    private final ObjectId articleId;
    private final List<TextDifference> differences;
    private final ObjectId editorId;

    public StandardArticleEdit(ObjectId id, ObjectId articleId, List<TextDifference> differences, ObjectId editorId) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(articleId);
        Preconditions.checkNotNull(differences);
        Preconditions.checkNotNull(editorId);

        this.id = id;
        this.articleId = articleId;
        this.differences = differences;
        this.editorId = editorId;
    }

    @Override
    public LocalDateTime getDateTime() {
        return id.getDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
