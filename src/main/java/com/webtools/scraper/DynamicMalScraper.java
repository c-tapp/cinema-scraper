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

public class DynamicMalScraper {

    private static final Duration WAIT_TIME = Duration.ofSeconds(5);

    public static void main(String[] args) {
        WebDriver driver = new FirefoxDriver();
        driver.get("https://myanimelist.net/anime/season");

        List<Map<String, Object>> Spring24TvNew = new ArrayList<>();
        List<Map<String, Object>> Spring24Movies = new ArrayList<>();

        try {
            WebElement tvNewContainer = new WebDriverWait(driver, WAIT_TIME)
                    .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.seasonal-anime-list:nth-child(1)")));
            List<WebElement> tvNewEntries = tvNewContainer.findElements(By.cssSelector("div.seasonal-anime.js-seasonal-anime"));
            Spring24TvNew = processEntries(tvNewEntries);

            WebElement movieContainer = new WebDriverWait(driver, WAIT_TIME)
                    .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.seasonal-anime-list:nth-child(6)")));
            List<WebElement> movieEntries = movieContainer.findElements(By.cssSelector("div.seasonal-anime.js-seasonal-anime"));
            Spring24Movies = processEntries(movieEntries);

            for (Map<String, Object> entry : Spring24TvNew) {
                System.out.println(entry);
            }

            for (Map<String, Object> entry : Spring24Movies) {
                System.out.println(entry);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    private static List<Map<String, Object>> processEntries(List<WebElement> animeEntries) {
        List<Map<String, Object>> entriesList = new ArrayList<>();

        for (WebElement entry : animeEntries) {
            Map<String, Object> animeMap = new LinkedHashMap<>();

            String titleJP = entry.findElement(By.cssSelector("h2.h2_anime_title")).getText();
            if (titleJP.isBlank()) {
                continue;
            }

            List<WebElement> h3Elements = entry.findElements(By.cssSelector("h3.h3_anime_subtitle"));
            String title = h3Elements.isEmpty() ? titleJP : h3Elements.get(0).getText();
            animeMap.put("Title", title);

            String startDate = entry.findElement(By.cssSelector("div.info span.item")).getText();
            animeMap.put("Start Date", startDate);

            List<String> genres = entry.findElements(By.cssSelector("div.genres-inner span.genre a"))
                    .stream().map(WebElement::getText).collect(Collectors.toList());
            animeMap.put("Genres", genres);

            String studio = "";
            WebElement studioElement = entry.findElement(By.cssSelector("div.property span.item"));
            if (studioElement.findElements(By.tagName("a")).size() > 0) {
                studio = studioElement.findElement(By.tagName("a")).getText();
            } else {
                studio = studioElement.getText();
            }
            animeMap.put("Studio", studio);

            entriesList.add(animeMap);
        }
        return entriesList;
    }
}
