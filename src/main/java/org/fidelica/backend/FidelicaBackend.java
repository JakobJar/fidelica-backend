package org.fidelica.backend;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.javalin.Javalin;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.fidelica.backend.repository.user.StandardUserRepository;
import org.fidelica.backend.rest.user.UserRegistrationController;
import org.fidelica.backend.user.login.PBKDFPasswordHandler;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static io.javalin.apibuilder.ApiBuilder.post;

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

        String mongoURI = System.getenv("MONGO_URI");
        if (mongoURI == null) {
            log.error("ENV \"MONGO_URI\" must be set.");
            return;
        }

        var mongoClient = createMongoClient(mongoURI);
        var mongoDatabase = mongoClient.getDatabase("fidelica-backend");
        log.info("Successfully connected to mongodb.");

        PBKDFPasswordHandler passwordHandler;
        try {
            passwordHandler = new PBKDFPasswordHandler();
        } catch (NoSuchAlgorithmException e) {
            log.error("Error while initializing password handler", e);
            return;
        }

        var userRepository = new StandardUserRepository(mongoDatabase);

        var host = System.getenv().getOrDefault("REST_HOST", "0.0.0.0");
        var port = Integer.parseInt(System.getenv().getOrDefault("REST_PORT", "80"));
        var app = Javalin.create().start(host, port);

        var userRegistrationController = new UserRegistrationController(userRepository, passwordHandler);
        app.routes(() -> {
            post("/register", userRegistrationController::createUser);
        });
    }

    private static MongoClient createMongoClient(@NonNull String mongoURI) {
        var defaultCodec = MongoClientSettings.getDefaultCodecRegistry();
        var pojoCodecProvider = PojoCodecProvider.builder()
                .automatic(true)
                .conventions(Arrays.asList(Conventions.ANNOTATION_CONVENTION, Conventions.CLASS_AND_PROPERTY_CONVENTION, Conventions.SET_PRIVATE_FIELDS_CONVENTION))
                .build();
        var codecRegistry = CodecRegistries.fromRegistries(defaultCodec, CodecRegistries.fromProviders(pojoCodecProvider));

        var settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoURI))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(codecRegistry)
                .build();

        return MongoClients.create(settings);
    }
}
