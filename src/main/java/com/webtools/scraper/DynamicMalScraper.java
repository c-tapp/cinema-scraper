package com.webtools.scraper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.time.Year;

public class DynamicMalScraper {

    private static final Duration WAIT_TIME = Duration.ofSeconds(5);
    private static final int TARGET_YEAR = 2024;
    private static final int TARGET_SEASON = 2;

    private WebDriver driver;
    private String targetURL = "https://myanimelist.net/anime/season/";
    private String season;

    /**
     * Constructor for Dynamic MAL Scraper.
     *
     * @param year release year
     * @param seasonNum release season (1-winter, 2-spring, 3-summer, 4-fall)
     */
    public DynamicMalScraper(int year, int seasonNum) {
        int currentYear = Year.now().getValue();
        this.driver = new FirefoxDriver();

        if (year < 1963 || year > currentYear) {
            closeDriver();
            throw new IllegalArgumentException("Year must be between 1963 and " + currentYear + ".");
        }

        if (seasonNum < 1 || seasonNum > 4){
            closeDriver();
            throw new IllegalArgumentException("Seasons must be between 1-4 (winter-fall).");
        }

        switch(seasonNum){
            case 1 -> season = "winter";
            case 2 -> season = "spring";
            case 3 -> season = "summer";
            case 4 -> season = "fall";
        }

        targetURL += year + "/" + season;
    }

    /**
     * Closes the WebDriver.
     */
    public void closeDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Fetches data for TV shows released in a specific year/season.
     *
     * @return a list of maps containing TV show data
     */
    public List<Map<String, Object>> getTvNew() {
        return scrapeEntries("div.seasonal-anime-list:nth-child(1)");
    }

    /**
     * Fetches data for movies released in a specific year/season.
     *
     * @return a list of maps containing movie data
     */
    public List<Map<String, Object>> getMovies() {
        return scrapeEntries("div.seasonal-anime-list:nth-child(6)");
    }

    /**
     * Performs web scraping to extract data of specific targetDiv.
     *
     * @param targetDiv where to scrape the data on the webpage
     * @return a list of maps, containing the key-value pairs representing the target data
     */
    private List<Map<String, Object>> scrapeEntries(String targetDiv) {
        List<Map<String, Object>> entries = new ArrayList<>();
        try {
            driver.get(targetURL);
            WebElement container = new WebDriverWait(driver, WAIT_TIME)
                    .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(targetDiv)));
            List<WebElement> malEntries = container.findElements(By.cssSelector("div.seasonal-anime.js-seasonal-anime"));
            entries = processEntries(malEntries);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entries;
    }

    /**
     * Processes a list of WebElement instances representing movies/tv shows to extract relevant data.
     *
     * @param animeEntries list of WebElement instances representing movies/tv shows
     * @return a list of maps, containing the key-value pairs representing the target data
     */
    private List<Map<String, Object>> processEntries(List<WebElement> animeEntries) {
        // Initialize list
        List<Map<String, Object>> entriesList = new ArrayList<>();

        // For each entry (show/movie)
        for (WebElement entry : animeEntries) {
            //Initialize map
            Map<String, Object> animeMap = new LinkedHashMap<>();

            // Find original title, skip entry if blank
            String titleJP = entry.findElement(By.cssSelector("h2.h2_anime_title")).getText();
            if (titleJP.isBlank()) {
                continue;
            }

            // Find english title if available, else use original title
            List<WebElement> h3Elements = entry.findElements(By.cssSelector("h3.h3_anime_subtitle"));
            String title = h3Elements.isEmpty() ? titleJP : h3Elements.get(0).getText();
            animeMap.put("title", title);

            // Find start date
            String startDate = entry.findElement(By.cssSelector("div.info span.item")).getText();
            animeMap.put("start_date", startDate);

            // Find list of genres
            List<String> genres = entry.findElements(By.cssSelector("div.genres-inner span.genre a"))
                    .stream().map(WebElement::getText).collect(Collectors.toList());
            animeMap.put("genres", genres);

            // Find studio, accounts for null values
            String studio = "";
            WebElement studioElement = entry.findElement(By.cssSelector("div.property span.item"));
            if (studioElement.findElements(By.tagName("a")).size() > 0) {
                studio = studioElement.findElement(By.tagName("a")).getText();  // Uses hyperlink text
            } else {
                studio = studioElement.getText();   // Uses plaintext
            }
            animeMap.put("studio", studio);

            // Find imageURL, if lazyLoad use data-src
            WebElement imageElement = entry.findElement(By.cssSelector("div.image a img"));
            String imageUrl = imageElement.getAttribute("src");
            if (imageUrl == null || imageUrl.isEmpty()) {
                imageUrl = imageElement.getAttribute("data-src");
            }
            animeMap.put("image_url", imageUrl);

            // Add map to list
            entriesList.add(animeMap);
        }
        return entriesList;
    }

    /**
     * Main method for testing purposes.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        DynamicMalScraper scraper = new DynamicMalScraper(TARGET_YEAR, TARGET_SEASON);
        try {
            List<Map<String, Object>> Spring24TvNew = scraper.getTvNew();
            List<Map<String, Object>> Spring24Movies = scraper.getMovies();

            Spring24TvNew.forEach(System.out::println);
            Spring24Movies.forEach(System.out::println);

        } finally {
            scraper.closeDriver();
        }
    }
}
