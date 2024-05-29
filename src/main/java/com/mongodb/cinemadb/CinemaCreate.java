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

/**
 * This class is responsible for populating MongoDB collections
 * with data scraped from web sources.
 */
public class CinemaCreate {

    /**
     * Release year for target data to be scraped.
     */
    private static final int year = 2024;
    /**
     * Release season (1-4:Winter-Fall) for target data to be scraped.
     */
    private static final int season = 2;

    /**
     * Main method runs the data populating process.
     *
     * Connects to the MongoDB database, scrapes data from the specified
     * year and season, and upserts into target collections.
     *
     * @param args
     */
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

    /**
     * Upserts documents into the specified collection
     *
     * Converts each map in the provided list into a Document, adds the release year and season,
     * and performs an upsert operation to update existing documents or insert new ones.
     *
     * @param collection
     * @param dataList
     */
    private static void upsertDocuments(MongoCollection<Document> collection, List<Map<String, Object>> dataList) {
        // Convert a list of maps into a list of MongoDB Documents
        List<Document> documents = dataList.stream().map(map -> {
            Document doc = new Document(map);
            doc.append("year", year);
            doc.append("season", season);
            return doc;
        }).toList();

        // Update existing Documents, or insert new, with year and season
        for (Document doc : documents) {
            Document filter = new Document("title", doc.getString("title"))
                    .append("year", doc.getInteger("year"))
                    .append("season", doc.getInteger("season"));
            collection.updateOne(filter, new Document("$set", doc), new UpdateOptions().upsert(true));
        }
    }
}
