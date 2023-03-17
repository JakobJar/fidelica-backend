package org.fidelica.backend.repository.repositories.post;

import org.bson.types.ObjectId;
import org.fidelica.backend.post.Post;
import org.fidelica.backend.post.history.CheckEdit;
import org.fidelica.backend.post.platform.twitter.Tweet;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    void create(Post post);

    Optional<Post> getById(ObjectId id);

    Optional<Tweet> getTweetById(long id);

    List<CheckEdit> getUncheckedPostEdits();
}
