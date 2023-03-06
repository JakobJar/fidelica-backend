package org.fidelica.backend.repository.repositories.post.twitter;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.NonNull;
import org.fidelica.backend.post.twitter.Tweet;

import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public class StandardTwitterRepository implements TwitterRepository {

    private final MongoCollection<Tweet> tweets;

    @Inject
    public StandardTwitterRepository(@NonNull MongoDatabase database) {
        this.tweets = database.getCollection("tweets", Tweet.class);
    }

    @Override
    public void create(Tweet tweet) {
        tweets.insertOne(tweet);
    }

    @Override
    public Optional<Tweet> getById(long id) {
        return Optional.ofNullable(tweets.find(eq("_id", id)).first());
    }
}
