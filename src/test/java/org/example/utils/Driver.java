package org.example.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.*;

public class Driver {

    // Можно переопределить через ENV:
    // SELENOID_URL=http://127.0.0.1:4444/wd/hub
    private static final String DEFAULT_SELENOID_URL = "http://127.0.0.1:4444/wd/hub";

    private static final String RAW_SELENOID_URL =
            System.getenv().getOrDefault("SELENOID_URL", DEFAULT_SELENOID_URL);

    // Если вдруг дали URL без /wd/hub — добавим
    private static final String SELENOID_URL =
            (RAW_SELENOID_URL.endsWith("/wd/hub") || RAW_SELENOID_URL.endsWith("/"))
                    ? RAW_SELENOID_URL.replaceAll("/+$", "") + (RAW_SELENOID_URL.endsWith("/wd/hub") ? "" : "/wd/hub")
                    : RAW_SELENOID_URL + "/wd/hub";

    private static final boolean IS_CI =
            "true".equalsIgnoreCase(System.getenv("CI")) ||
                    "true".equalsIgnoreCase(System.getenv("GITHUB_ACTIONS"));

    public static WebDriver getAutoLocalDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = buildLocalChromeOptions();
        WebDriver driver = new ChromeDriver(options);
        applyTimeouts(driver);
        return driver;
    }

    public static WebDriver getRemoteOrLocalDriver() throws MalformedURLException {
        try {
            return getRemoteDriver();
        } catch (Exception e) {
            System.err.println("Remote is not available: " + SELENOID_URL);
            System.err.println("Falling back to LOCAL Chrome. Reason: " +
                    e.getClass().getSimpleName() + " - " + e.getMessage());
            return getAutoLocalDriver();
        }
    }

    public static RemoteWebDriver getRemoteDriver() throws MalformedURLException {
        ChromeOptions options = buildRemoteChromeOptions();
        URL gridUrl = URI.create(SELENOID_URL).toURL();
        RemoteWebDriver driver = new RemoteWebDriver(gridUrl, options);
        applyTimeouts(driver);
        return driver;
    }

    // ===== OPTIONS =====

    private static ChromeOptions buildLocalChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");

        // Для GitHub Actions / Linux runner
        if (IS_CI) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
        }
        return options;
    }

    private static ChromeOptions buildRemoteChromeOptions() {
        ChromeOptions options = new ChromeOptions();

        // версия должна совпадать с browsers.json и образом
        options.setBrowserVersion("128.0");

        // аргументы для контейнера
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        Map<String, Object> selenoid = new HashMap<>();
        selenoid.put("name", "FormTest");                 // имя сессии
        selenoid.put("sessionTimeout", "15m");
        selenoid.put("env", Collections.singletonList("TZ=UTC"));

        Map<String, Object> labels = new HashMap<>();
        labels.put("manual", "true");
        selenoid.put("labels", labels);

        selenoid.put("enableVideo", true);
        selenoid.put("enableVNC", true);
        selenoid.put("enableLog", true);

        // ВАЖНО: НЕ ставим headless в selenoid options.
        // Видео-recorder стабильнее, когда браузер не headless.
        // selenoid.put("headless", true);

        // Чтобы видео называлось понятно (иначе sessionId)
        selenoid.put("videoName", "FormTest.mp4");

        options.setCapability("selenoid:options", selenoid);
        return options;
    }

    private static void applyTimeouts(WebDriver driver) {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
    }
}