package com.mongodb.quickstart;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertManyOptions;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Create {

    private static final Random rand = new Random();

    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create(System.getProperty("mongodb.uri"))) {

            MongoDatabase sampleTrainingDB = mongoClient.getDatabase("sample_training");
            MongoCollection<Document> gradesCollection = sampleTrainingDB.getCollection("grades");

            insertOneDocument(gradesCollection);
            insertManyDocuments(gradesCollection);
        }
    }

    private static void insertOneDocument(MongoCollection<Document> gradesCollection) {
        gradesCollection.insertOne(generateNewGrade(10002d, 1d));
        System.out.println("One grade inserted for studentId 10002.");
    }

    private static void insertManyDocuments(MongoCollection<Document> gradesCollection) {
        List<Document> grades = new ArrayList<>();
        for (double classId = 1d; classId <= 10d; classId++) {
            grades.add(generateNewGrade(10003d, classId));
        }

        gradesCollection.insertMany(grades, new InsertManyOptions().ordered(false));
        System.out.println("Ten grades inserted for studentId 10003.");
    }

    private static Document generateNewGrade(double studentId, double classId) {
        List<Document> scores = List.of(
                new Document("type", "exam").append("score", Math.round((50 + rand.nextDouble() * 50) * 1000.0) / 1000.0),
                new Document("type", "quiz").append("score", Math.round((50 + rand.nextDouble() * 50) * 1000.0) / 1000.0),
                new Document("type", "homework").append("score", Math.round((50 + rand.nextDouble() * 50) * 1000.0) / 1000.0),
                new Document("type", "homework").append("score", Math.round((50 + rand.nextDouble() * 50) * 1000.0) / 1000.0)
        );
        return new Document("_id", new ObjectId()).append("student_id", studentId)
                .append("class_id", classId)
                .append("scores", scores);
    }

}