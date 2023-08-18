package org.fidelica.backend.rest.routes.post;

import com.google.inject.Inject;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UnauthorizedResponse;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.fidelica.backend.post.Post;
import org.fidelica.backend.post.PostRating;
import org.fidelica.backend.post.StandardPostAnnotation;
import org.fidelica.backend.post.url.PostURLProvider;
import org.fidelica.backend.repository.repositories.post.PostRepository;
import org.fidelica.backend.user.User;
import org.fidelica.backend.user.permission.UserPermissionProcessor;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class PostAnnotationController {

    private final PostRepository postRepository;
    private final Set<PostURLProvider> postURLProvider;

    private final UserPermissionProcessor permissionProcessor;

    @Inject
    public PostAnnotationController(@NonNull PostRepository postRepository, @NonNull Set<PostURLProvider> postURLProvider,
                                    @NonNull UserPermissionProcessor permissionProcessor) {
        this.postRepository = postRepository;
        this.postURLProvider = postURLProvider;
        this.permissionProcessor = permissionProcessor;
    }

    public void getByURL(@NonNull Context context) {
        var url = context.pathParam("url");

        var postProvider = getProvider(url);

        var post = postProvider.getPostByURL(url).orElseThrow(() -> new NotFoundResponse("Post not found."));
        var annotations = postRepository.findAnnotationsByPostId(post.getId());

        context.json(annotations);
    }

    public void annotatePost(@NonNull Context context) {
        var url = context.formParam("postURL");
        var rawRating = context.formParam("rating");
        var comment = context.formParam("comment");
        var rawRelatedArticles = context.formParams("relatedArticles");

        if (url == null || rawRating == null || comment == null)
            throw new BadRequestResponse("Missing required parameters.");

        PostRating rating;
        try {
            rating = PostRating.valueOf(rawRating.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BadRequestResponse("Rating is invalid.");
        }

        Set<ObjectId> relatedArticles;
        try {
            relatedArticles = rawRelatedArticles.stream()
                    .map(ObjectId::new)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } catch (IllegalArgumentException e) {
            throw new BadRequestResponse("Contains invalid related annotation id: " + e.getMessage());
        }

        User user = context.sessionAttribute("user");
        if (!permissionProcessor.hasPermission(user, "annotation.create"))
            throw new UnauthorizedResponse("You don't have permission to create an annotation.");

        var postProvider = getProvider(url);
        Post post = postProvider.getPostByURL(url).orElse(null);
        if (post == null)
            post = postProvider.createPost(url);

        var annotation = new StandardPostAnnotation(ObjectId.get(), post.getId(), rating, comment, relatedArticles, user.getId());
        postRepository.createAnnotation(annotation);
        context.json(annotation);
    }

    public void getAnnotationById(@NonNull Context context) {
        ObjectId id;
        try {
            id = new ObjectId(context.pathParam("id"));
        } catch (IllegalArgumentException e) {
            throw new BadRequestResponse("Invalid annotation id.");
        }

        var annotation = postRepository.findAnnotationById(id).orElseThrow(() -> new NotFoundResponse("Annotation not found."));

        context.json(annotation);
    }

    public void upvoteAnnotation(@NonNull Context context) {
        voteAnnotation(context, VoteType.UPVOTE);
    }

    public void downvoteAnnotation(@NonNull Context context) {
        voteAnnotation(context, VoteType.DOWNVOTE);
    }

    public void removeAnnotationVote(@NonNull Context context) {
        voteAnnotation(context, VoteType.REMOVE);
    }

    protected void voteAnnotation(@NonNull Context context, VoteType type) {
        ObjectId annotationId;
        try {
            annotationId = new ObjectId(context.pathParam("id"));
        } catch (IllegalArgumentException e) {
            throw new BadRequestResponse("Invalid annotation id.");
        }

        User user = context.sessionAttribute("user");
        if (!permissionProcessor.hasPermission(user, "annotation.vote"))
            throw new UnauthorizedResponse("You don't have permission to vote.");

        var success = switch (type) {
            case UPVOTE -> postRepository.upvoteAnnotation(annotationId, user.getId());
            case DOWNVOTE -> postRepository.downvoteAnnotation(annotationId, user.getId());
            case REMOVE -> postRepository.removeAnnotationVote(annotationId, user.getId());
        };
        if (!success)
            throw new NotFoundResponse("Annotation not found.");

        context.result("Success");
    }

    private PostURLProvider<?> getProvider(@NonNull String url) {
        return postURLProvider.stream()
                .filter(provider -> provider.matches(url))
                .findFirst()
                .orElseThrow(() -> new BadRequestResponse("Post url is not supported or invalid."));
    }

    private enum VoteType {

        UPVOTE,
        DOWNVOTE,
        REMOVE;
    }
}
