package org.example.utils;

import com.google.common.base.Function;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);
    private static final long DEFAULT_TIMEOUT = 10;

    public static WebElement fluentWait(WebDriver driver, By by) {
        return fluentWait(driver, by, DEFAULT_TIMEOUT);
    }

    public static WebElement fluentWait(WebDriver driver, By by, long seconds) {
        logger.info("Waiting for element: {} with timeout: {} seconds", by, seconds);
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(seconds))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class);
        return wait.until((Function<WebDriver, WebElement>) dr -> {
            assert dr != null;
            return dr.findElement(by);
        });
    }

    public static <T> T explicitWait(WebDriver driver, ExpectedCondition<T> condition, long seconds) {
        logger.info("Waiting with explicit wait: {} seconds", seconds);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        return wait.until(condition);
    }
}
