package org.fidelica.backend.post;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import org.fidelica.backend.post.platform.twitter.TwitterPostURLProvider;
import org.fidelica.backend.post.url.PostURLProvider;

public class PostModule extends AbstractModule {

    @Override
    protected void configure() {
        var postURLProviderBinder = Multibinder.newSetBinder(binder(), PostURLProvider.class);
        postURLProviderBinder.addBinding().to(TwitterPostURLProvider.class).in(Scopes.SINGLETON);
    }
}
