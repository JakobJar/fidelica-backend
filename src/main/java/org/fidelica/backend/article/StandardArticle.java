package org.fidelica.backend.article;

import lombok.*;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.Locale;

@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@BsonDiscriminator("StandardArticle")
public class StandardArticle implements Article {

    @BsonId
    private final ObjectId id;
    private String title;
    private String shortDescription;
    private ArticleRating rating;
    private String content;
    private final Locale language;

    private boolean visible;
    private boolean editable;

    public StandardArticle(ObjectId id, String title, String description, ArticleRating rating, String content, Locale language) {
        this(id, title, description, rating, content, language, false, true);
    }

    public StandardArticle(@NonNull ObjectId id, @NonNull String title, @NonNull String description,
                           @NonNull ArticleRating rating, @NonNull String content,
                           @NonNull Locale language, boolean visible, boolean editable) {
        this.id = id;
        this.title = title;
        this.shortDescription = description;
        this.rating = rating;
        this.content = content;
        this.language = language;
        this.visible = visible;
        this.editable = editable;
    }
}
