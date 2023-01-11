package org.fidelica.backend;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;

@Slf4j
public class FidelicaBackend {

    public static void main(String[] args) {
        log.info("""
                                
                  __ _     _      _ _
                 / _(_)   | |    | (_)
                | |_ _  __| | ___| |_  ___ __ _
                |  _| |/ _` |/ _ \\ | |/ __/ _` |
                | | | | (_| |  __/ | | (_| (_| |
                |_| |_|\\__,_|\\___|_|_|\\___\\__,_|
                """);

        String mongoURI = System.getenv("MONGO_URL");
        if (mongoURI == null) {
            log.error("ENV \"MONGO_URL\" must be set.");
            return;
        }

        var mongoClient = createMongoClient(mongoURI);
    }

    private static MongoClient createMongoClient(@NonNull String mongoURI) {
        var defaultCodec = MongoClientSettings.getDefaultCodecRegistry();
        var pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        var codecRegistry = CodecRegistries.fromRegistries(defaultCodec, CodecRegistries.fromProviders(pojoCodecProvider));

        var settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoURI))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(codecRegistry)
                .build();

        return MongoClients.create(settings);
    }
}
