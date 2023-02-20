package org.fidelica.backend.rest.post;

import com.google.inject.Inject;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.fidelica.backend.post.PostCheckRating;
import org.fidelica.backend.post.StandardPostCheck;
import org.fidelica.backend.post.url.PostURLProvider;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class PostController {

    private final Set<PostURLProvider> postURLProvider;

    @Inject
    public PostController(@NonNull Set<PostURLProvider> postURLProvider) {
        this.postURLProvider = postURLProvider;
    }

    public void getByURL(@NonNull Context context) {
        var url = context.pathParam("url");

        var postProvider = getProvider(url);

        context.json(postProvider.getPostByURL(url));
    }

    // Only for testing
    public void reportPost(@NonNull Context context) {
        var url = context.formParam("url");
        var rawRating = context.formParam("rating");
        var comment = context.formParam("comment");
        var rawRelatedFactChecks = context.formParams("relatedArticles");

        PostCheckRating rating;
        try {
            rating = PostCheckRating.valueOf(rawRating.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new BadRequestResponse("Rating is invalid.");
        }

        Set<ObjectId> relatedFactChecks;
        try {
            relatedFactChecks = rawRelatedFactChecks.stream()
                    .map(ObjectId::new)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } catch (IllegalArgumentException e) {
            throw new BadRequestResponse("Contains invalid related fact check id: " + e.getMessage());
        }

        var postProvider = getProvider(url);
        postProvider.createPost(url, new StandardPostCheck(rating, comment, relatedFactChecks));
        context.json("Success.");
    }

        private PostURLProvider<?> getProvider(@NonNull String url) {
            return postURLProvider.stream()
                    .filter(provider -> provider.matches(url))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestResponse("Post url is not supported or invalid."));
    }
}
