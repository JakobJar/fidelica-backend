package org.fidelica.backend.post.platform.twitter;

import lombok.*;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@BsonDiscriminator("StandardTweet")
public class StandardTweet implements Tweet {

    @BsonId
    private final ObjectId id;

    private final long tweetId;

    public StandardTweet(@NonNull ObjectId id, long tweetId) {
        this.id = id;
        this.tweetId = tweetId;
    }
}
