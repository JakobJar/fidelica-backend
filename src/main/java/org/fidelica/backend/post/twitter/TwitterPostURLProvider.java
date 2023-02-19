package org.fidelica.backend.post.twitter;

import com.google.inject.Inject;
import lombok.NonNull;
import org.fidelica.backend.post.PostCheck;
import org.fidelica.backend.post.url.PostURLProvider;
import org.fidelica.backend.repository.post.twitter.TwitterRepository;

import java.util.Optional;
import java.util.regex.Pattern;

public class TwitterPostURLProvider implements PostURLProvider<Tweet> {

    private final TwitterRepository repository;
    private final Pattern tweetPattern;

    @Inject
    public TwitterPostURLProvider(TwitterRepository repository) {
        this.repository = repository;

        this.tweetPattern = Pattern.compile("https:\\\\/\\\\/twitter\\\\.com\\\\/\\\\w+\\\\/status\\\\/(\\\\d+)");
    }

    @Override
    public Optional<Tweet> getPostByURL(@NonNull String url) {
        var id = getId(url);
        return repository.getById(id);
    }

    @Override
    public void createPost(@NonNull String url, PostCheck check) {
        var id = getId(url);

        var tweet = new StandardTweet(id, check);
        repository.create(tweet);
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
