package org.fidelica.backend.repository.repositories.post;

import org.bson.types.ObjectId;
import org.fidelica.backend.post.Post;
import org.fidelica.backend.post.PostCheckRating;
import org.fidelica.backend.post.history.CheckEdit;
import org.fidelica.backend.post.platform.twitter.Tweet;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PostRepository {

    void create(Post post);

    boolean update(ObjectId id, String note, PostCheckRating rating, Collection<ObjectId> relatedArticles);

    boolean updateVisibility(ObjectId id, boolean visible);

    Optional<Post> findById(ObjectId id);

    Optional<Tweet> findTweetById(long id);

    Optional<CheckEdit> findCheckEditById(ObjectId editId);

    boolean isFirstEdit(ObjectId postId, ObjectId id);

    List<CheckEdit> getUncheckedPostEdits();

    boolean checkEdit(ObjectId id, boolean approve, ObjectId checkerId, String comment);
}
