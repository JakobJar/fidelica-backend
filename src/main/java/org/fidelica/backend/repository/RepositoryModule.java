package org.fidelica.backend.repository;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import org.fidelica.backend.repository.repositories.article.ArticleRepository;
import org.fidelica.backend.repository.repositories.article.StandardArticleRepository;
import org.fidelica.backend.repository.repositories.post.PostRepository;
import org.fidelica.backend.repository.repositories.post.StandardPostRepository;
import org.fidelica.backend.repository.repositories.user.GroupRepository;
import org.fidelica.backend.repository.repositories.user.StandardGroupRepository;
import org.fidelica.backend.repository.repositories.user.StandardUserRepository;
import org.fidelica.backend.repository.repositories.user.UserRepository;

public class RepositoryModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(UserRepository.class).to(StandardUserRepository.class).in(Scopes.SINGLETON);
        bind(GroupRepository.class).to(StandardGroupRepository.class).in(Scopes.SINGLETON);
        bind(ArticleRepository.class).to(StandardArticleRepository.class).in(Scopes.SINGLETON);

        bind(PostRepository.class).to(StandardPostRepository.class).in(Scopes.SINGLETON);
    }
}
