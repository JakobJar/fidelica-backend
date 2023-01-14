package org.fidelica.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.javalin.Javalin;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.fidelica.backend.repository.user.StandardUserRepository;
import org.fidelica.backend.repository.user.UserRepository;
import org.fidelica.backend.rest.access.AccessAuthenticationRole;
import org.fidelica.backend.rest.access.RestAccessManager;
import org.fidelica.backend.rest.json.AnnotationExcludeStrategy;
import org.fidelica.backend.rest.json.GsonMapper;
import org.fidelica.backend.rest.json.ObjectIdAdapter;
import org.fidelica.backend.rest.user.UserAuthenticationController;
import org.fidelica.backend.user.StandardUser;
import org.fidelica.backend.user.User;
import org.fidelica.backend.user.login.PBKDFPasswordHandler;
import org.fidelica.backend.user.login.PasswordHandler;
import org.fidelica.backend.user.login.PasswordHash;
import org.fidelica.backend.user.login.SaltedPasswordHash;
import org.fidelica.backend.util.DummyGoogleRecaptcha;
import org.fidelica.backend.util.GoogleRecaptcha;
import org.fidelica.backend.util.GoogleRecaptchaV3;

import java.net.http.HttpClient;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static io.javalin.apibuilder.ApiBuilder.*;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
        new FidelicaBackend().start();
    }

    private MongoDatabase mongoDatabase;
    private Javalin app;
    private Gson gson;
    private HttpClient httpClient;

    private PasswordHandler passwordHandler;
    private GoogleRecaptcha googleRecaptcha;

    private UserRepository userRepository;

    private UserAuthenticationController userAuthenticationController;

    public void start() {
        String mongoURI = System.getenv("MONGO_URI");
        if (mongoURI == null) {
            log.error("ENV \"MONGO_URI\" must be set.");
            return;
        }

        var mongoClient = createMongoClient(mongoURI);
        mongoDatabase = mongoClient.getDatabase("fidelica-backend");
        log.info("Successfully connected to mongodb.");

        try {
            passwordHandler = new PBKDFPasswordHandler();
        } catch (NoSuchAlgorithmException e) {
            log.error("Error while initializing password handler", e);
            return;
        }

        gson = new GsonBuilder()
                .addSerializationExclusionStrategy(new AnnotationExcludeStrategy())
                .registerTypeAdapter(ObjectId.class, new ObjectIdAdapter())
                .disableHtmlEscaping()
                .create();

        httpClient = HttpClient.newHttpClient();

        var recaptchaKey = System.getenv("RECAPTCHA_KEY");
        googleRecaptcha = recaptchaKey != null ? new GoogleRecaptchaV3(httpClient, gson, recaptchaKey) : new DummyGoogleRecaptcha();

        registerRepositories();

        var host = System.getenv().getOrDefault("REST_HOST", "0.0.0.0");
        var port = Integer.parseInt(System.getenv().getOrDefault("REST_PORT", "80"));
        app = Javalin.create(config -> {
            config.jsonMapper(new GsonMapper(gson));
            config.accessManager(new RestAccessManager());
        }).start(host, port);

        registerRoutes();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            app.stop();
            mongoClient.close();
        }));
    }

    private void registerRoutes() {
        userAuthenticationController = new UserAuthenticationController(userRepository, passwordHandler, googleRecaptcha);

        app.routes(() -> {
            before("*", context -> {
                context.header("Access-Control-Allow-Origin", "*");
            });
            post("/register", userAuthenticationController::register, AccessAuthenticationRole.ANONYMOUS);
            post("/login", userAuthenticationController::login,  AccessAuthenticationRole.ANONYMOUS);
            get("/logout", userAuthenticationController::logout);
        });
    }

    private void registerRepositories() {
        userRepository = new StandardUserRepository(mongoDatabase);
    }

    private MongoClient createMongoClient(@NonNull String mongoURI) {
        var defaultCodec = MongoClientSettings.getDefaultCodecRegistry();
        var pojoCodecProvider = PojoCodecProvider.builder()
                .automatic(true)
                .register(User.class, StandardUser.class, PasswordHash.class, SaltedPasswordHash.class)
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
