package org.fidelica.backend.rest.json;

import com.google.gson.Gson;
import io.javalin.json.JsonMapper;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

public record GsonMapper(Gson gson) implements JsonMapper {

    @NotNull
    @Override
    public <T> T fromJsonStream(@NotNull InputStream json, @NotNull Type targetType) {
        try (var reader = new InputStreamReader(json)) {
            return gson.fromJson(reader, targetType);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @NotNull
    @Override
    public <T> T fromJsonString(@NotNull String json, @NotNull Type targetType) {
        return gson.fromJson(json, targetType);
    }

    @NotNull
    @Override
    public InputStream toJsonStream(@NotNull Object obj, @NotNull Type type) {
        return new ByteArrayInputStream(toJsonString(obj, type).getBytes());
    }

    @NotNull
    @Override
    public String toJsonString(@NotNull Object obj, @NotNull Type type) {
        return gson.toJson(obj, type);
    }
}
