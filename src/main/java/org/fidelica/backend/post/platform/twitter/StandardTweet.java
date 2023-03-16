package org.fidelica.backend.post.platform.twitter;

import lombok.*;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.fidelica.backend.post.PostCheck;

@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@BsonDiscriminator("StandardTweet")
public class StandardTweet implements Tweet {

    @BsonId
    private final long id;
    private PostCheck check;

    public StandardTweet(long id, @NonNull PostCheck check) {
        this.id = id;
        this.check = check;
    }
}
