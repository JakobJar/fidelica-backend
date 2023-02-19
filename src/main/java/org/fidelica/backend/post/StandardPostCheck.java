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

        private PostCheckRating rating;
        private String comment;
        private final Collection<ObjectId> relatedFactChecks;

        public StandardPostCheck(PostCheckRating rating, String comment) {
            this(rating, comment, new LinkedHashSet<>());
        }

        public StandardPostCheck(@NonNull PostCheckRating rating, String comment, @NonNull Collection<ObjectId> relatedFactChecks) {
            this.rating = rating;
            this.comment = comment;
            this.relatedFactChecks = relatedFactChecks;
        }
}
