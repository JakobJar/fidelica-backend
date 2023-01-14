package org.fidelica.backend.rest.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bson.types.ObjectId;

import java.io.IOException;

public class ObjectIdAdapter extends TypeAdapter<ObjectId> {

    @Override
    public void write(JsonWriter jsonWriter, ObjectId objectId) throws IOException {
        jsonWriter.value(objectId.toHexString());
    }

    @Override
    public ObjectId read(JsonReader jsonReader) throws IOException {
        return new ObjectId(jsonReader.nextString());
    }
}
