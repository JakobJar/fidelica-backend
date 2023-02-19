package org.fidelica.backend.rest.post;

import com.google.inject.Inject;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import lombok.NonNull;
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

        var postProvider = postURLProvider.stream()
                .filter(provider -> provider.matches(url))
                .findFirst()
                .orElseThrow(() -> new BadRequestResponse("Post url is not supported or invalid."));

        context.json(postProvider.getPostByURL(url));
    }
}
