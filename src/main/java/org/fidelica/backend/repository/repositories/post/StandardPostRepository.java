package org.fidelica.backend.repository.repositories.post;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import lombok.NonNull;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.fidelica.backend.post.Post;
import org.fidelica.backend.post.PostCheckRating;
import org.fidelica.backend.post.history.CheckEdit;
import org.fidelica.backend.post.platform.twitter.Tweet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
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
    public void create(@NonNull Post post) {
        posts.insertOne(post);
    }

    @Override
    public boolean update(ObjectId id, String note, PostCheckRating rating, Collection<ObjectId> relatedArticles) {
        List<Bson> changes = new ArrayList<>();
        if (note != null)
            changes.add(Updates.set("check.note", note));
        if (rating != null)
            changes.add(Updates.set("check.rating", rating));
        changes.add(Updates.set("check.relatedArticles", relatedArticles));

        return posts.updateOne(eq("_id", id), Updates.combine(changes)).wasAcknowledged();
    }

    @Override
    public boolean updateVisibility(ObjectId id, boolean visible) {
        return posts.updateOne(eq("_id", id), Updates.set("check.visible", visible)).wasAcknowledged();
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
    public Optional<CheckEdit> findCheckEditById(ObjectId editId) {
        return Optional.ofNullable(edits.find(eq("_id", editId)).first());
    }

    @Override
    public boolean isFirstEdit(@NonNull ObjectId postId, @NonNull ObjectId id) {
        var result = edits.find(eq("postId", postId))
                .projection(Projections.include("_id"))
                .into(new ArrayList<>());

        return result.size() == 1 && result.get(0).getId().equals(id);
    }

    @Override
    public List<CheckEdit> getUncheckedPostEdits() {
        return edits.find(eq("edit.checkerId", null)).into(new ArrayList<>());
    }

    @Override
    public boolean checkEdit(ObjectId id, boolean approve, ObjectId checkerId, String comment) {
        var changes = Updates.combine(Updates.set("approve", approve),
                Updates.set("checkerId", checkerId),
                Updates.set("comment", comment));
        return edits.updateOne(and(eq("_id", id), eq("checkerId", null)), changes)
                .wasAcknowledged();
    }
}
