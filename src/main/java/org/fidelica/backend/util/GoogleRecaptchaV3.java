package org.fidelica.backend.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public record GoogleRecaptchaV3(HttpClient httpClient, Gson gson, String secretKey) implements GoogleRecaptcha {

    private static final String RECAPTCHA_SERVICE_URL = "https://www.google.com/recaptcha/api/siteverify";
    private static final float REQUIRED_SCORE = 0.5f;

    @Override
    public boolean isValid(String clientResponse) throws IOException, InterruptedException {
        if (clientResponse == null || clientResponse.isBlank())
            return false;

        var body = HttpRequest.BodyPublishers.ofString("secret=" + URLEncoder.encode(secretKey, StandardCharsets.UTF_8)
                + "&response=" + URLEncoder.encode(clientResponse, StandardCharsets.UTF_8));
        var request = HttpRequest.newBuilder(URI.create(RECAPTCHA_SERVICE_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .timeout(Duration.ofSeconds(10))
                .POST(body)
                .build();

        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() / 100 != 2)
            return false;

        var json = gson.fromJson(response.body(), JsonObject.class);
        var success = json.get("success").getAsBoolean();
        var score = json.get("score").getAsFloat();

        return success && score >= REQUIRED_SCORE;
    }
}
