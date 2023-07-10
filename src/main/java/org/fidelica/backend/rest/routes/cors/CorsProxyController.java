package org.fidelica.backend.rest.routes.cors;

import com.google.inject.Inject;
import io.javalin.http.Context;
import io.javalin.http.InternalServerErrorResponse;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class CorsProxyController {

    private final HttpClient httpClient;

    @Inject
    public CorsProxyController(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void removeCorsHeaders(@NotNull Context context) throws InterruptedException {
        var url = context.queryParam("url");
        var request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new InternalServerErrorResponse("Failed to fetch response: " + e.getMessage());
        }

        var headers = new HashMap<String, String>();
        response.headers().map().forEach((key, value) -> {
            headers.put(key.toLowerCase(), String.join(",", value));
        });
        headers.remove("access-control-allow-origin");
        headers.remove("access-control-allow-credentials");
        headers.remove("access-control-allow-headers");
        headers.remove("access-control-allow-methods");

        context.headerMap().clear();
        context.headerMap().putAll(headers);

        context.result(response.body());
    }
}
