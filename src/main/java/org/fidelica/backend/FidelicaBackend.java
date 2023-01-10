package org.fidelica.backend;

import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;

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

        String mongoURL = System.getenv("MONGO_URL");
        if (mongoURL == null) {
            log.error("ENV \"MONGO_URL\" must be set.");
            return;
        }

        var mongoClient = MongoClients.create(mongoURL);
    }
}
