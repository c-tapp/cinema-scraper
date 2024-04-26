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

    public static void main(String[] args) {

        try {

            // Create a new instance of the FirefoxDriver
            WebDriver driver = new FirefoxDriver();

            // Navigate to the webpage
            driver.get("https://myanimelist.net/anime/season");

            // Wait for the dynamic content to load
            List<WebElement> titleTextDivs = new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.title-text")));

            // Iterate through each div and get h2 and h3
            for (WebElement div : titleTextDivs) {
                String h2Title = div.findElement(By.cssSelector("h2.h2_anime_title")).getText();
                if (h2Title.isBlank()){
                    continue;
                }
                List<WebElement> h3Elements = div.findElements(By.cssSelector("h3.h3_anime_subtitle"));
                String h3Title = h3Elements.isEmpty() ? h2Title : h3Elements.get(0).getText();

                System.out.println(h3Title);

            }

            // Close the browser
            driver.quit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
