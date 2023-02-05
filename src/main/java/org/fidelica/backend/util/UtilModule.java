package org.fidelica.backend.util;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class UtilModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(GoogleRecaptcha.class).toProvider(RecaptchaProvider.class).in(Scopes.SINGLETON);
    }
}
