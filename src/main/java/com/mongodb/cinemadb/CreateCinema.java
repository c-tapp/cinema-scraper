package com.mongodb.cinemadb;

import com.mongodb.client.model.UpdateOptions;
import com.webtools.scraper.DynamicMalScraper;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CreateCinema {

    private static int year = 2024;
    private static int season = 2;

    public static void main(String[] args){
        try (MongoClient client = MongoClients.create(System.getProperty("mongodb.uri"))) {
            MongoDatabase cinemaDB = client.getDatabase("cinema");
            MongoCollection<Document> seasonalAnime = cinemaDB.getCollection("seasonal_anime");

            DynamicMalScraper scraper = new DynamicMalScraper(year, season);
            List<Map<String, Object>> animeList = scraper.getTvNew();
            List<Document> documents = animeList.stream()
                    .map(map -> {
                        Document doc = new Document(map);
                        doc.append("Year", year);
                        doc.append("Season", season);
                        return doc;
                    })
                    .collect(Collectors.toList());

            for (Document doc : documents) {
                Document filter = new Document("Title", doc.getString("Title"))
                        .append("Year", doc.getInteger("Year"))
                        .append("Season", doc.getInteger("Season"));
                seasonalAnime.updateOne(filter, new Document("$set", doc), new UpdateOptions().upsert(true));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
