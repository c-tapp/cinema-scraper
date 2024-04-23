package com.mongodb.quickstart;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;

public class Connection {
    public Connection() {
    }

    public static void main(String[] args) {
        String connectionString = System.getProperty("mongodb.uri");
        MongoClient mongoClient = MongoClients.create(connectionString);

        try {
            System.out.println("=> Connection successful: " + preFlightChecks(mongoClient));
            System.out.println("=> Print list of databases:");
            List<Document> databases = (List)mongoClient.listDatabases().into(new ArrayList());
            databases.forEach((db) -> {
                System.out.println(db.toJson());
            });
        } catch (Throwable var6) {
            if (mongoClient != null) {
                try {
                    mongoClient.close();
                } catch (Throwable var5) {
                    var6.addSuppressed(var5);
                }
            }

            throw var6;
        }

        if (mongoClient != null) {
            mongoClient.close();
        }

    }

    static boolean preFlightChecks(MongoClient mongoClient) {
        Document pingCommand = new Document("ping", 1);
        Document response = mongoClient.getDatabase("admin").runCommand(pingCommand);
        System.out.println("=> Print result of the '{ping: 1}' command.");
        System.out.println(response.toJson(JsonWriterSettings.builder().indent(true).build()));
        return ((Number)response.get("ok", Number.class)).intValue() == 1;
    }
}
