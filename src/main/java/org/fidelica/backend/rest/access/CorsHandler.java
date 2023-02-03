package org.fidelica.backend.rest.access;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CorsHandler implements Handler {

    private static final List<String> ALLOWED_ORIGINS = List.of("https://fidelica.org", "http://127.0.0.1:8080", "http://localhost:8080");

    @Override
    public void handle(@NotNull Context context) throws Exception {
        var origin = context.header("Origin");

        String allowedOrigin;
        if (ALLOWED_ORIGINS.contains(origin)) {
            allowedOrigin = origin;
        } else {
            allowedOrigin = ALLOWED_ORIGINS.get(0);
        }

        context.header("Access-Control-Allow-Origin", allowedOrigin);
        context.header("Access-Control-Allow-Credentials", "true");
    }
}
