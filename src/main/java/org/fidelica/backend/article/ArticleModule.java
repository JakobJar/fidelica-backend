package org.fidelica.backend.article;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import org.fidelica.backend.article.history.difference.LCSTextDifferenceProcessor;
import org.fidelica.backend.article.history.difference.TextDifferenceProcessor;

public class ArticleModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(TextDifferenceProcessor.class).to(LCSTextDifferenceProcessor.class).in(Scopes.SINGLETON);
    }
}
