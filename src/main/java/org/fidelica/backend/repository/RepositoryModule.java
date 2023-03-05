package org.fidelica.backend.repository;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import org.fidelica.backend.repository.article.FactCheckRepository;
import org.fidelica.backend.repository.article.StandardFactCheckRepository;
import org.fidelica.backend.repository.post.twitter.StandardTwitterRepository;
import org.fidelica.backend.repository.post.twitter.TwitterRepository;
import org.fidelica.backend.repository.user.GroupRepository;
import org.fidelica.backend.repository.user.StandardGroupRepository;
import org.fidelica.backend.repository.user.StandardUserRepository;
import org.fidelica.backend.repository.user.UserRepository;

public class RepositoryModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(UserRepository.class).to(StandardUserRepository.class).in(Scopes.SINGLETON);
        bind(GroupRepository.class).to(StandardGroupRepository.class).in(Scopes.SINGLETON);
        bind(FactCheckRepository.class).to(StandardFactCheckRepository.class).in(Scopes.SINGLETON);

        bind(TwitterRepository.class).to(StandardTwitterRepository.class).in(Scopes.SINGLETON);
    }
}
