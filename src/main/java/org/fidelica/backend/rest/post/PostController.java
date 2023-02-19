package org.fidelica.backend.rest.post;

import com.google.inject.Inject;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import lombok.NonNull;
import org.fidelica.backend.post.PostCheckRating;
import org.fidelica.backend.post.StandardPostCheck;
import org.fidelica.backend.post.url.PostURLProvider;

import java.util.Set;

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
    public void createPostCheck(@NonNull Context context) {
        var url = context.formParam("url");
        var rawRating = context.formParam("rating");
        var comment = context.formParam("comment");

        PostCheckRating rating;
        try {
            rating = PostCheckRating.valueOf(rawRating);
        } catch (IllegalArgumentException e) {
            throw new BadRequestResponse("Rating is invalid.");
        }

        var postProvider = getProvider(url);
        postProvider.createPost(url, new StandardPostCheck(rating, comment));
        context.json("Success.");
    }

        private PostURLProvider<?> getProvider(@NonNull String url) {
        return postURLProvider.stream()
                .filter(provider -> provider.matches(url))
                .findFirst()
                .orElseThrow(() -> new BadRequestResponse("Post url is not supported or invalid."));
    }
}
