package org.fidelica.backend.article;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.Locale;

@Data
@EqualsAndHashCode(of = "id")
@BsonDiscriminator("StandardArticle")
public class StandardArticle implements Article {

    @BsonId
    private final ObjectId id;
    private String title;
    private String shortDescription;
    private String content;
    private final Locale language;

    private boolean visible;
    private boolean editable;

    public StandardArticle(ObjectId id, String title, String shortDescription, String content, Locale language) {
        this(id, title, shortDescription, content, language, false, true);
    }

    @BsonCreator
    public StandardArticle(@NonNull @BsonId ObjectId id, @NonNull @BsonProperty("title") String title,
                           @NonNull @BsonProperty("shortDescription") String shortDescription,
                           @NonNull @BsonProperty("shortDescription") String content,
                           @NonNull @BsonProperty("language") Locale language, @BsonProperty("visible") boolean visible,
                           @BsonProperty("editable") boolean editable) {
        this.id = id;
        this.title = title;
        this.shortDescription = shortDescription;
        this.content = content;
        this.language = language;
        this.visible = visible;
        this.editable = editable;
    }
}
