package org.fidelica.backend.rest.routes.post;

import com.google.inject.Inject;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UnauthorizedResponse;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.fidelica.backend.post.Post;
import org.fidelica.backend.post.PostCheckRating;
import org.fidelica.backend.post.StandardPostCheck;
import org.fidelica.backend.post.url.PostURLProvider;
import org.fidelica.backend.repository.repositories.post.PostRepository;
import org.fidelica.backend.user.User;
import org.fidelica.backend.user.permission.UserPermissionProcessor;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class PostCheckController {

    private final PostRepository postRepository;
    private final Set<PostURLProvider> postURLProvider;

    private final UserPermissionProcessor permissionProcessor;

    @Inject
    public PostCheckController(@NonNull PostRepository postRepository, @NonNull Set<PostURLProvider> postURLProvider,
                               @NonNull UserPermissionProcessor permissionProcessor) {
        this.postRepository = postRepository;
        this.postURLProvider = postURLProvider;
        this.permissionProcessor = permissionProcessor;
    }

    public void getById(@NonNull Context context) {
        ObjectId id;
        try {
            id = new ObjectId(context.pathParam("postId"));
        } catch (IllegalArgumentException e) {
            throw new BadRequestResponse("Invalid post id.");
        }

        var post = postRepository.findById(id).orElseThrow(() -> new NotFoundResponse("Post not found."));
        var checks = postRepository.findChecksById(post.getId());

        context.json(checks);
    }

    public void getByURL(@NonNull Context context) {
        var url = context.pathParam("url");

        var postProvider = getProvider(url);

        var post = postProvider.getPostByURL(url).orElseThrow(() -> new NotFoundResponse("Post not found."));
        var checks = postRepository.findChecksById(post.getId());

        context.json(checks);
    }

    public void reportPost(@NonNull Context context) {
        var url = context.formParam("postURL");
        var rawRating = context.formParam("rating");
        var comment = context.formParam("comment");
        var rawRelatedArticles = context.formParams("relatedArticles");

        PostCheckRating rating;
        try {
            rating = PostCheckRating.valueOf(rawRating.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new BadRequestResponse("Rating is invalid.");
        }

        Set<ObjectId> relatedArticles;
        try {
            relatedArticles = rawRelatedArticles.stream()
                    .map(ObjectId::new)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } catch (IllegalArgumentException e) {
            throw new BadRequestResponse("Contains invalid related fact check id: " + e.getMessage());
        }

        var postProvider = getProvider(url);
        Post post = postProvider.getPostByURL(url).orElse(null);
        if (post == null)
            post = postProvider.createPost(url);

        var postCheck = new StandardPostCheck(ObjectId.get(), post.getId(), rating, comment, relatedArticles);
        postRepository.createCheck(postCheck);
        context.json(postCheck);
    }

    public void upvoteCheck(@NonNull Context context) {
        voteCheck(context, true);
    }

    public void downvoteCheck(@NonNull Context context) {
        voteCheck(context, false);
    }

    protected void voteCheck(@NonNull Context context, @NonNull boolean upvote) {
        ObjectId checkId;
        try {
            checkId = new ObjectId(context.pathParam("checkId"));
        } catch (IllegalArgumentException e) {
            throw new BadRequestResponse("Invalid check id.");
        }

        User user = context.sessionAttribute("user");
        if (!permissionProcessor.hasPermission(user, "postCheck.vote"))
            throw new UnauthorizedResponse("You don't have permission to vote.");

        var success = (upvote) ? postRepository.upvoteCheck(checkId, user.getId())
                : postRepository.downvoteCheck(checkId, user.getId());
        if (!success)
            throw new NotFoundResponse("Check not found.");

        context.result("Success");
    }

    private PostURLProvider<?> getProvider(@NonNull String url) {
        return postURLProvider.stream()
                .filter(provider -> provider.matches(url))
                .findFirst()
                .orElseThrow(() -> new BadRequestResponse("Post url is not supported or invalid."));
    }
}
