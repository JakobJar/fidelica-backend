package org.fidelica.backend.repository.repositories.post;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.fidelica.backend.post.Post;
import org.fidelica.backend.post.PostCheck;
import org.fidelica.backend.post.platform.twitter.Tweet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public class StandardPostRepository implements PostRepository {

    private final MongoCollection<Post> posts;
    private final MongoCollection<PostCheck> checks;

    @Inject
    public StandardPostRepository(@NonNull MongoDatabase database) {
        this.posts = database.getCollection("posts", Post.class);
        this.checks = database.getCollection("checks", PostCheck.class);
    }

    @Override
    public void create(@NonNull Post post) {
        posts.insertOne(post);
    }

    @Override
    public void createCheck(@NonNull PostCheck check) {
        checks.insertOne(check);
    }

    @Override
    public Optional<Post> findById(@NonNull ObjectId id) {
        return Optional.ofNullable(posts.find(eq("_id", id)).first());
    }

    @Override
    public Optional<Tweet> findTweetById(long id) {
        return Optional.ofNullable((Tweet) posts.find(eq("tweetId", id)).first());
    }

    @Override
    public List<PostCheck> findChecksById(@NonNull ObjectId id) {
        return checks.find(eq("postId", id)).into(new ArrayList<>());
    }

    @Override
    public boolean upvoteCheck(@NonNull ObjectId id, @NonNull ObjectId userId) {
        var updates = Updates.combine(Updates.addToSet("upvotes", id), Updates.pull("downvotes", id));
        return checks.updateOne(eq("_id", id), updates).getMatchedCount() > 0;
    }

    @Override
    public boolean downvoteCheck(@NonNull ObjectId id, @NonNull ObjectId userId) {
        var updates = Updates.combine(Updates.pull("upvotes", id), Updates.addToSet("downvotes", id));
        return checks.updateOne(eq("_id", id), updates).getMatchedCount() > 0;
    }
}
