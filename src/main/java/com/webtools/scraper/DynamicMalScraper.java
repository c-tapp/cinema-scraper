package com.webtools.scraper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class DynamicMalScraper {

    private static final Duration WAIT_TIME = Duration.ofSeconds(5);

    public static void main(String[] args) {
        WebDriver driver = new FirefoxDriver();
        driver.get("https://myanimelist.net/anime/season");

        try {
            WebElement tvNewContainer = new WebDriverWait(driver, WAIT_TIME)
                    .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.seasonal-anime-list:nth-child(1)")));
            List<WebElement> tvNewEntries = tvNewContainer.findElements(By.cssSelector("div.seasonal-anime.js-seasonal-anime"));

            WebElement movieContainer = new WebDriverWait(driver, WAIT_TIME)
                    .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.seasonal-anime-list:nth-child(6)")));
            List<WebElement> movieEntries = movieContainer.findElements(By.cssSelector("div.seasonal-anime.js-seasonal-anime"));

            // Process each anime entry
            processEntries(tvNewEntries);
            processEntries(movieEntries);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    private static void processEntries(List<WebElement> animeEntries) {
        for (WebElement entry : animeEntries) {
            // Get original title (JP)
            String title = entry.findElement(By.cssSelector("h2.h2_anime_title")).getText();
            if (title.isBlank()) {
                continue;
            }
            // If english title exists and is not blank - set title to (EN)
            List<WebElement> h3Title = entry.findElements(By.cssSelector("h3.h3_anime_subtitle"));
            String titleEN = h3Title.isEmpty() ? title : h3Title.get(0).getText();

            // Get start date
            String startDate = entry.findElement(By.cssSelector("div.info span.item")).getText();

            // Get list of genres
            List<String> genres = entry.findElements(By.cssSelector("div.genres-inner span.genre a"))
                    .stream().map(WebElement::getText).collect(Collectors.toList());

            // Get studio - accounts for null entries
            String studio = "";
            WebElement studioElement = entry.findElement(By.cssSelector("div.property span.item"));
            if (studioElement.findElements(By.tagName("a")).size() > 0) {
                studio = studioElement.findElement(By.tagName("a")).getText();  // If there's a link, get the linked text
            } else {
                studio = studioElement.getText();  // Otherwise, get the plain text
            }
            System.out.println("Title: " + titleEN + " | Start Date: " + startDate + " | Genres: " + genres + " | Studio: " + studio);
        }
    }
}
