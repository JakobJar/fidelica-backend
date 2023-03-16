package org.fidelica.backend.repository.repositories.post.twitter;

import org.fidelica.backend.post.platform.twitter.Tweet;

import java.util.Optional;

public interface TwitterRepository {

    void create(Tweet tweet);

    Optional<Tweet> getById(long id);
}
