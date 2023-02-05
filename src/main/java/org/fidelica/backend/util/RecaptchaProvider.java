package org.fidelica.backend.util;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import java.net.http.HttpClient;

public class RecaptchaProvider implements Provider<GoogleRecaptcha> {

    private final HttpClient httpClient;
    private final Gson gson;
    private final String secretKey;

    @Inject
    public RecaptchaProvider(HttpClient httpClient, Gson gson, @Named("RECAPTCHA KEY") String secretKey) {
        this.httpClient = httpClient;
        this.gson = gson;
        this.secretKey = secretKey;
    }

    @Override
    public GoogleRecaptcha get() {
        if (null != secretKey && !secretKey.isBlank())
            return new GoogleRecaptchaV3(httpClient, gson, secretKey);
        return new DummyGoogleRecaptcha();
    }
}
