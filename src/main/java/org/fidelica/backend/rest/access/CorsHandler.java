package org.fidelica.backend.rest.access;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CorsHandler implements Handler {

    private static final List<String> ALLOWED_ORIGINS = List.of("https://fidelica.org", "http://127.0.0.1:8080/", "http://localhost:8080");

    @Override
    public void handle(@NotNull Context context) throws Exception {
        var referer = context.header("Referer");

        String origin;
        if (ALLOWED_ORIGINS.contains(referer)) {
            origin = referer;
        } else {
            origin = ALLOWED_ORIGINS.get(0);
        }

        context.header("Access-Control-Allow-Origin", origin);
        context.header("Access-Control-Allow-Credentials", "true");
    }
}
