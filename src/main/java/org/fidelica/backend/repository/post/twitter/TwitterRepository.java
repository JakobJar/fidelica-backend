package org.fidelica.backend.repository.post.twitter;

import org.fidelica.backend.post.twitter.Tweet;

import java.util.Optional;

public interface TwitterRepository {

    void create(Tweet tweet);

    Optional<Tweet> getById(long id);
}
