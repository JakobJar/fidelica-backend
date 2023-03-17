package org.fidelica.backend.repository.repositories.post;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.fidelica.backend.post.Post;
import org.fidelica.backend.post.history.CheckEdit;
import org.fidelica.backend.post.platform.twitter.Tweet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public class StandardPostRepository implements PostRepository {

    private final MongoCollection<Post> posts;
    private final MongoCollection<CheckEdit> edits;

    @Inject
    public StandardPostRepository(@NonNull MongoDatabase database) {
        this.posts = database.getCollection("posts", Post.class);
        this.edits = database.getCollection("edits", CheckEdit.class);
    }

    @Override
    public void create(Post post) {
        posts.insertOne(post);
    }

    @Override
    public Optional<Post> getById(ObjectId id) {
        return Optional.ofNullable(posts.find(eq("_id", id)).first());
    }

    @Override
    public Optional<Tweet> getTweetById(long id) {
        return Optional.of((Tweet) posts.find(eq("tweetId", id)).first());
    }

    @Override
    public List<CheckEdit> getUncheckedPostEdits() {
        return edits.find(eq("edit.checkerId", null)).into(new ArrayList<>());
    }
}
