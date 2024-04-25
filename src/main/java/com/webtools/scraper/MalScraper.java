package com.webtools.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MalScraper {
    public static void main(String[] args) {
        try {
            // Connect to the webpage and get the HTML content
            Document doc = Jsoup.connect("https://myanimelist.net/anime/season").get();

            // Select all div elements with the class "title-text"
            Elements titleDivs = doc.select("div.title-text");

            // Create a list to store the extracted titles
            List<String> titles = new ArrayList<>();

            // Iterate over each div element
            for (Element div : titleDivs) {
                // Extract the text from the h2 and h3 elements inside the div
                String title = div.select("h2.h2_anime_title").text();
                String subtitle = div.select("h3.h3_anime_subtitle").text();

                // Combine the title and subtitle (if present)
                if (!subtitle.isEmpty()) {
                    title += ": " + subtitle;
                }

                // Add the combined title to the list
                titles.add(title);
            }

            // Print the extracted titles
            for (String title : titles) {
                System.out.println(title);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
