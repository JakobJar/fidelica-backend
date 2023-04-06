package org.fidelica.backend.repository.repositories.post;

import org.bson.types.ObjectId;
import org.fidelica.backend.post.Post;
import org.fidelica.backend.post.PostCheck;
import org.fidelica.backend.post.platform.twitter.Tweet;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    void create(Post post);

    void createCheck(PostCheck check);

    Optional<Post> findById(ObjectId id);

    Optional<Tweet> findTweetById(long id);

    List<PostCheck> findChecksById(ObjectId id);

    boolean upvoteCheck(ObjectId id, ObjectId userId);

    boolean downvoteCheck(ObjectId id, ObjectId userId);
}
