package org.fidelica.backend.post.url;

import org.fidelica.backend.post.Post;

import java.util.Optional;

public interface PostURLProvider<T extends Post> {

    Optional<T> getPostByURL(String url);

    T createPost(String url);

    boolean matches(String url);
}
