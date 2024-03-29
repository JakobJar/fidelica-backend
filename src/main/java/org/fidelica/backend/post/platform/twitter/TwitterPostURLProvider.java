package org.fidelica.backend.post.platform.twitter;

import com.google.inject.Inject;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.fidelica.backend.post.url.PostURLProvider;
import org.fidelica.backend.repository.repositories.post.PostRepository;

import java.util.Optional;
import java.util.regex.Pattern;

public class TwitterPostURLProvider implements PostURLProvider<Tweet> {

    private final PostRepository repository;
    private final Pattern tweetPattern;

    @Inject
    public TwitterPostURLProvider(PostRepository repository) {
        this.repository = repository;

        this.tweetPattern = Pattern.compile("^https?://(?:www\\.)?twitter\\.com/(?:#!/)?[a-zA-Z0-9_]{1,15}/status/(\\d+)(?:\\\\?.*)?$");
    }

    @Override
    public Optional<Tweet> getPostByURL(@NonNull String url) {
        var id = getId(url);
        return repository.findTweetById(id);
    }

    @Override
    public Tweet createPost(@NonNull String url) {
        var id = getId(url);

        var tweet = new StandardTweet(ObjectId.get(), id);
        repository.create(tweet);

        return tweet;
    }

    private long getId(@NonNull String url) {
        var matcher = tweetPattern.matcher(url);
        if (matcher.find())
            return Long.parseLong(matcher.group(1));

        throw new IllegalArgumentException("Couldn't resolve valid tweet-id from url: " + url);
    }

    @Override
    public boolean matches(@NonNull String url) {
        return tweetPattern.matcher(url).matches();
    }
}
