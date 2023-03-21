package org.fidelica.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.*;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
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
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.server.session.DefaultSessionCache;
import org.eclipse.jetty.server.session.NullSessionDataStore;
import org.eclipse.jetty.server.session.SessionHandler;
import org.fidelica.backend.article.ArticleModule;
import org.fidelica.backend.post.PostModule;
import org.fidelica.backend.repository.RepositoryModule;
import org.fidelica.backend.repository.serialization.LocaleCodec;
import org.fidelica.backend.rest.access.AccessRole;
import org.fidelica.backend.rest.access.RestAccessManager;
import org.fidelica.backend.rest.json.AnnotationExcludeStrategy;
import org.fidelica.backend.rest.json.GsonMapper;
import org.fidelica.backend.rest.json.ObjectIdAdapter;
import org.fidelica.backend.rest.routes.article.ArticleController;
import org.fidelica.backend.rest.routes.article.ArticleEditController;
import org.fidelica.backend.rest.routes.moderation.ArticleModerationController;
import org.fidelica.backend.rest.routes.moderation.PostModerationController;
import org.fidelica.backend.rest.routes.post.PostController;
import org.fidelica.backend.rest.routes.user.UserAuthenticationController;
import org.fidelica.backend.rest.routes.user.UserController;
import org.fidelica.backend.user.UserModule;
import org.fidelica.backend.user.group.GroupManager;
import org.fidelica.backend.util.UtilModule;

import java.net.http.HttpClient;
import java.util.Arrays;

import static io.javalin.apibuilder.ApiBuilder.*;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FidelicaBackend extends AbstractModule {

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

    @Override
    protected void configure() {
        bindConstant().annotatedWith(Names.named("MONGO URI")).to(System.getenv("MONGO_URI"));
        bindConstant().annotatedWith(Names.named("RECAPTCHA KEY")).to(System.getenv("RECAPTCHA_KEY"));
        bind(HttpClient.class).toInstance(HttpClient.newHttpClient());
    }

    public void start() {
        var stage = Stage.valueOf(System.getenv().getOrDefault("STAGE", "PRODUCTION"));
        var injector = Guice.createInjector(stage, this, new UserModule(),
                new UtilModule(), new RepositoryModule(), new ArticleModule(), new PostModule());

        var groupManager = injector.getInstance(GroupManager.class);
        groupManager.reload();

        var app = injector.getInstance(Javalin.class);

        registerRoutes(app, injector);

        var host = System.getenv().getOrDefault("REST_HOST", "0.0.0.0");
        var port = Integer.parseInt(System.getenv().getOrDefault("REST_PORT", "80"));
        app.start(host, port);
        log.info("Started REST server on {}:{}", host, port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down...");
            app.stop();
            injector.getInstance(MongoClient.class).close();
        }));
    }

    private void registerRoutes(Javalin app, Injector injector) {
        var userAuthenticationController = injector.getInstance(UserAuthenticationController.class);
        var userController = injector.getInstance(UserController.class);

        var articleController = injector.getInstance(ArticleController.class);
        var articleEditController = injector.getInstance(ArticleEditController.class);
        var articleModerationController = injector.getInstance(ArticleModerationController.class);

        var postController = injector.getInstance(PostController.class);
        var postModerationController = injector.getInstance(PostModerationController.class);

        app.routes(() -> {
            path("/auth", () -> {
                post("/register", userAuthenticationController::register, AccessRole.ANONYMOUS);
                post("/login", userAuthenticationController::login, AccessRole.ANONYMOUS);
                get("/logout", userAuthenticationController::logout, AccessRole.AUTHENTICATED);
            });

            path("/user", () -> {
                get(userController::getCurrentUser, AccessRole.AUTHENTICATED);
                get("/{id}", userController::getUserById);
            });

            path("/report", () -> {
                post(postController::reportPost, AccessRole.AUTHENTICATED);

            });

            path("/check", () -> {
                get("/<url>", postController::getByURL);
            });

            path("/article", () -> {
                post(articleController::createArticle, AccessRole.AUTHENTICATED);
                path("/{articleId}", () -> {
                    get(articleController::getArticleById);
                    get("/edits", articleEditController::getEditPreviews);
                    path("/edit", () -> {
                        post(articleEditController::createEdit, AccessRole.AUTHENTICATED);
                        path("/{editId}", () -> {
                            get(articleEditController::getEditById);
                            post("/check", articleModerationController::checkEdit, AccessRole.AUTHENTICATED);
                        });
                    });
                });
            });

            path("/moderation", () -> {
                get("/edits", articleModerationController::getPendingEdits, AccessRole.AUTHENTICATED);
                get("/reports", postModerationController::getPendingEdits, AccessRole.AUTHENTICATED);
            });
        });
    }

    @Provides
    @Singleton
    private Javalin createApp(Gson gson) {
        return Javalin.create(config -> {
            config.jsonMapper(new GsonMapper(gson));
            config.accessManager(new RestAccessManager());
            config.jetty.sessionHandler(this::createSessionHandler);

            config.plugins.enableHttpAllowedMethodsOnRoutes();
            config.plugins.enableCors(corsContainer -> {
                corsContainer.add(corsConfig -> {
                    corsConfig.allowHost("https://fidelica.org", "http://localhost:8080",
                            "http://127.0.0.1:8080", "http://localhost:3000", "http://127.0.0.1:3000");
                    corsConfig.allowCredentials = true;
                });
            });
        });
    }

    @Provides
    @Singleton
    private MongoClient createMongoClient(@NonNull @Named("MONGO URI") String mongoURI) {
        var conventions = Arrays.asList(Conventions.ANNOTATION_CONVENTION,
                Conventions.CLASS_AND_PROPERTY_CONVENTION, Conventions.SET_PRIVATE_FIELDS_CONVENTION);
        var packages = new String[] { "org.fidelica.backend.user",
                "org.fidelica.backend.user.login", "org.fidelica.backend.user.group", "org.fidelica.backend.article",
                "org.fidelica.backend.article.history", "org.fidelica.backend.article.history.difference",
                "org.fidelica.backend.post", "org.fidelica.backend.post.twitter"};

        var defaultCodec = MongoClientSettings.getDefaultCodecRegistry();
        var pojoCodecProvider = PojoCodecProvider.builder()
                .automatic(true)
                .register(packages)
                .conventions(conventions)
                .build();
        var codecRegistry = CodecRegistries.fromRegistries(defaultCodec,
                CodecRegistries.fromCodecs(new LocaleCodec()),
                CodecRegistries.fromProviders(pojoCodecProvider));

        var settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoURI))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(codecRegistry)
                .build();

        var mongoClient = MongoClients.create(settings);
        log.info("Successfully connected to MongoDB.");
        return mongoClient;
    }

    @Provides
    @Singleton
    private MongoDatabase getDatabase(@NonNull MongoClient mongoClient) {
        return mongoClient.getDatabase("fidelica-backend");
    }

    @Provides
    @Singleton
    private Gson createGson() {
        return new GsonBuilder()
                .addSerializationExclusionStrategy(new AnnotationExcludeStrategy())
                .registerTypeAdapter(ObjectId.class, new ObjectIdAdapter())
                .disableHtmlEscaping()
                .create();
    }

    private SessionHandler createSessionHandler() {
        var sessionHandler = new SessionHandler();
        sessionHandler.setSameSite(HttpCookie.SameSite.STRICT);
        sessionHandler.setSessionCookie("FIDELICA_SESSION");

        var sessionCache = new DefaultSessionCache(sessionHandler);
        sessionCache.setSessionDataStore(new NullSessionDataStore());
        sessionHandler.setSessionCache(sessionCache);

        return sessionHandler;
    }
}
