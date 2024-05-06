package com.mongodb.cinemadb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import com.webtools.scraper.DynamicMalScraper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CinemaCreate {

    private static final int year = 2024;
    private static final int season = 2;

    public static void main(String[] args){
        try (MongoClient client = MongoClients.create(System.getProperty("mongodb.uri"))) {
            MongoDatabase cinemaDB = client.getDatabase("cinema");
            MongoCollection<Document> seasonalAnime = cinemaDB.getCollection("seasonal_anime");
            MongoCollection<Document> movies = cinemaDB.getCollection("movies");

            DynamicMalScraper scraper = new DynamicMalScraper(year, season);
            List<Map<String, Object>> animeList = scraper.getTvNew();
            upsertDocuments(seasonalAnime, animeList);

            List<Map<String, Object>> movieList = scraper.getMovies();
            upsertDocuments(movies, movieList);

            scraper.closeDriver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void upsertDocuments(MongoCollection<Document> collection, List<Map<String, Object>> dataList) {
        List<Document> documents = dataList.stream().map(map -> {
            Document doc = new Document(map);
            doc.append("year", year);
            doc.append("season", season);
            return doc;
        }).toList();

        for (Document doc : documents) {
            Document filter = new Document("title", doc.getString("title"))
                    .append("year", doc.getInteger("year"))
                    .append("season", doc.getInteger("season"));
            collection.updateOne(filter, new Document("$set", doc), new UpdateOptions().upsert(true));
        }
    }
}
