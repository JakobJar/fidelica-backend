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

    private final ObjectId id;
    private final ObjectId postId;

    private final PostCheckRating rating;
    private final String note;
    private final Collection<ObjectId> relatedArticles;
    private final ObjectId reporterId;

    private final Collection<ObjectId> upvoters;
    private final Collection<ObjectId> downvoters;

    public StandardPostCheck(ObjectId id, ObjectId postId, PostCheckRating rating, String comment, Collection<ObjectId> relatedArticles, ObjectId reporterId) {
        this(id, postId, rating, comment, relatedArticles, reporterId, new LinkedHashSet<>(), new LinkedHashSet<>());
    }

    public StandardPostCheck(@NonNull ObjectId id, @NonNull ObjectId postId, @NonNull PostCheckRating rating,
                             String note, @NonNull Collection<ObjectId> relatedArticles, @NonNull ObjectId reporterId,
                             @NonNull Collection<ObjectId> upvoters, @NonNull Collection<ObjectId> downvoters) {
        this.id = id;
        this.postId = postId;
        this.rating = rating;
        this.note = note;
        this.relatedArticles = relatedArticles;
        this.reporterId = reporterId;
        this.upvoters = upvoters;
        this.downvoters = downvoters;
    }
}
