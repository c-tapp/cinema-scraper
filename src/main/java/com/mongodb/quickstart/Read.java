package com.mongodb.quickstart;

import com.mongodb.client.*;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.descending;

public class Read {

    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create(System.getProperty("mongodb.uri"))) {
            MongoDatabase sampleTrainingDB = mongoClient.getDatabase("sample_training");
            MongoCollection<Document> gradesCollection = sampleTrainingDB.getCollection("grades");

            Document student1 = gradesCollection.find(new Document("student_id", 10003)).first();
            if (student1 != null) {
                JsonWriterSettings prettyPrint = JsonWriterSettings.builder().indent(true).build();
                System.out.println("Student 1: " + student1.toJson(prettyPrint));
            } else {
                System.out.println("No document found for student_id 10003.");
            }
        }
    }
}