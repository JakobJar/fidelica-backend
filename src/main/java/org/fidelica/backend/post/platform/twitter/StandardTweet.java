package org.fidelica.backend.post.platform.twitter;

import lombok.*;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import org.fidelica.backend.post.PostCheck;

@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@BsonDiscriminator("StandardTweet")
public class StandardTweet implements Tweet {

    @BsonId
    private final ObjectId id;

    private final long tweetId;
    private PostCheck check;

    public StandardTweet(ObjectId id, long tweetId, @NonNull PostCheck check) {
        this.id = id;
        this.tweetId = tweetId;
        this.check = check;
    }
}
