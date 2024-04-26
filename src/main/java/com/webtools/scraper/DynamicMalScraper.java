package com.webtools.scraper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class DynamicMalScraper {

    private static final Duration WAIT_TIME = Duration.ofSeconds(5);

    public static void main(String[] args) {

        // Create a new instance of the FirefoxDriver
        WebDriver driver = new FirefoxDriver();

        // Navigate to the webpage
        driver.get("https://myanimelist.net/anime/season");

        try {
            // nth-child(1) = TV (New)
            WebElement tvNewContainer = new WebDriverWait(driver, WAIT_TIME)
                    .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.seasonal-anime-list:nth-child(1)")));
            List<WebElement> tvNewTitles = tvNewContainer.findElements(By.cssSelector("div.title-text"));

            // nth-child(6) = Movies
            WebElement movieContainer = new WebDriverWait(driver, WAIT_TIME)
                    .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.seasonal-anime-list:nth-child(6)")));
            List<WebElement> movieTitles = movieContainer.findElements(By.cssSelector("div.title-text"));

            // Process each section's divs
            processTitles(tvNewTitles);
            processTitles(movieTitles);

        } catch (Exception e) {
            // Print errors
            e.printStackTrace();
        } finally {
            // Close browser
            driver.quit();
        }
    }

    private static void processTitles(List<WebElement> titleTextDivs) {
        for (WebElement div : titleTextDivs) {
            // Get original title
            String h2Title = div.findElement(By.cssSelector("h2.h2_anime_title")).getText();
            // If div title is blank, skip
            if (h2Title.isBlank()){
                continue;
            }
            // Get English title
            List<WebElement> h3Elements = div.findElements(By.cssSelector("h3.h3_anime_subtitle"));
            // If English title is not blank, replace original with English
            String h3Title = h3Elements.isEmpty() ? h2Title : h3Elements.get(0).getText();
            System.out.println(h3Title);
        }
    }
}
