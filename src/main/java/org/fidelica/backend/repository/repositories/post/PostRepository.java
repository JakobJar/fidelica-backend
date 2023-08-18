package org.fidelica.backend.repository.repositories.post;

import org.bson.types.ObjectId;
import org.fidelica.backend.post.Post;
import org.fidelica.backend.post.PostAnnotation;
import org.fidelica.backend.post.platform.twitter.Tweet;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    void create(Post post);

    void createAnnotation(PostAnnotation check);

    Optional<Post> findById(ObjectId id);

    Optional<PostAnnotation> findAnnotationById(ObjectId id);

    Optional<Tweet> findTweetById(long id);

    List<PostAnnotation> findAnnotationsByPostId(ObjectId postId);

    boolean upvoteAnnotation(ObjectId id, ObjectId userId);

    boolean downvoteAnnotation(ObjectId id, ObjectId userId);

    boolean removeAnnotationVote(ObjectId id, ObjectId userId);
}
