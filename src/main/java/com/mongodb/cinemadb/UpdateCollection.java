package com.mongodb.cinemadb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * This class is responsible for updating MongoDB collections
 */
public class UpdateCollection {

    /**
     * Main method connects to the database and runs the update process
     * by calling the appropriate methods.
     *
     * @param args
     */
    public static void main(String[] args) {
        try (MongoClient client = MongoClients.create(System.getProperty("mongodb.uri"))) {
            MongoDatabase cinemaDB = client.getDatabase("cinema");
            MongoCollection<Document> seasonalAnime = cinemaDB.getCollection("seasonal_anime");
            MongoCollection<Document> movies = cinemaDB.getCollection("movies");

            addFieldDescription(seasonalAnime);
            addFieldDescription(movies);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Update all documents in the collection to add a description field with an empty string
     *
     * @param collection The MongoDB collection to update
     */
    private static void addFieldDescription(MongoCollection<Document> collection) {
        Document update = new Document("$set", new Document("description", ""));
        collection.updateMany(new Document(), update);
    }
}
