package org.fidelica.backend.util;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import java.net.http.HttpClient;

public class UtilModule extends AbstractModule {

    @Provides
    @Singleton
    private GoogleRecaptcha getRecaptcha(HttpClient httpClient, Gson gson, @Named("RECAPTCHA KEY") String secretKey) {
        if (secretKey != null && !secretKey.isBlank()) {
            return new GoogleRecaptchaV3(httpClient, gson, secretKey);
        }
        return new DummyGoogleRecaptcha();
    }
}
