package org.fidelica.backend.article;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.Locale;

@Data
@EqualsAndHashCode(of = "id")
@BsonDiscriminator("StandardArticle")
public class StandardArticle implements Article {

    @BsonId
    private final ObjectId id;
    private final String title;
    private final String shortDescription;
    private final String content;
    private final Locale language;


}
