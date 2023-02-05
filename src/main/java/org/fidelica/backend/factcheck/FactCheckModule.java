package org.fidelica.backend.factcheck;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import org.fidelica.backend.factcheck.history.difference.LCSTextDifferenceProcessor;
import org.fidelica.backend.factcheck.history.difference.TextDifferenceProcessor;

public class FactCheckModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(TextDifferenceProcessor.class).to(LCSTextDifferenceProcessor.class).in(Scopes.SINGLETON);
    }
}
