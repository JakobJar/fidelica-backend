package org.fidelica.backend.repository;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import org.fidelica.backend.repository.article.FactCheckRepository;
import org.fidelica.backend.repository.article.StandardFactCheckRepository;
import org.fidelica.backend.repository.user.StandardUserRepository;
import org.fidelica.backend.repository.user.UserRepository;

public class RepositoryModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(UserRepository.class).to(StandardUserRepository.class).in(Scopes.SINGLETON);
        bind(FactCheckRepository.class).to(StandardFactCheckRepository.class).in(Scopes.SINGLETON);
    }
}
