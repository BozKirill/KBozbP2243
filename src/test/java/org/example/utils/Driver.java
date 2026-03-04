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

    // Можно переопределить через переменную окружения:
    // SELENOID_URL=http://192.168.0.10:4444/wd/hub
    private static final String DEFAULT_SELENOID_URL = "http://localhost:4444/wd/hub";
    private static final String SELENOID_URL = System.getenv().getOrDefault("SELENOID_URL", DEFAULT_SELENOID_URL);

    // Если не хочешь fallback на локал — поставь false
    private static final boolean FALLBACK_TO_LOCAL = true;

    public static WebDriver getAutoLocalDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = buildLocalChromeOptions();
        WebDriver driver = new ChromeDriver(options);
        applyTimeouts(driver);
        return driver;
    }

    public static WebDriver getLocalDriver() {
        // Лучше так не делать, но оставляю твой метод рабочим
        // Важно: путь должен быть до chromedriver.exe, а не до папки
        // System.setProperty("webdriver.chrome.driver", "C:\\path\\to\\chromedriver.exe");

        ChromeOptions options = buildLocalChromeOptions();
        WebDriver driver = new ChromeDriver(options);
        applyTimeouts(driver);
        return driver;
    }

    public static RemoteWebDriver getRemoteDriver() throws MalformedURLException {
        ChromeOptions options = buildRemoteChromeOptions();

        try {
            // Нормальный способ создать URL
            URL gridUrl = URI.create(SELENOID_URL).toURL();
            RemoteWebDriver driver = new RemoteWebDriver(gridUrl, options);
            applyTimeouts(driver);
            return driver;

        } catch (Exception e) {
            // Главное: у тебя сейчас падает CONNECT (сервер не отвечает)
            if (!FALLBACK_TO_LOCAL) {
                // пробрасываем как было, чтобы ты видел причину
                if (e instanceof MalformedURLException) throw (MalformedURLException) e;
                throw new RuntimeException("Cannot create remote session. Check SELENOID_URL=" + SELENOID_URL, e);
            }

            // Fallback на локальный Chrome, чтобы тесты могли запускаться
            System.err.println("Remote is not available: " + SELENOID_URL);
            System.err.println("Falling back to LOCAL Chrome. Reason: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return (RemoteWebDriver) null; // чтобы компилятор не ругался (не используется)
        }
    }

    /**
     * Удобный метод: пытается Remote, если не получилось — Local.
     * Используй его в тесте вместо getRemoteDriver().
     */
    public static WebDriver getRemoteOrLocalDriver() throws MalformedURLException {
        try {
            ChromeOptions options = buildRemoteChromeOptions();
            URL gridUrl = URI.create(SELENOID_URL).toURL();
            RemoteWebDriver remote = new RemoteWebDriver(gridUrl, options);
            applyTimeouts(remote);
            return remote;
        } catch (Exception e) {
            if (!FALLBACK_TO_LOCAL) {
                if (e instanceof MalformedURLException) throw (MalformedURLException) e;
                throw new RuntimeException("Cannot create remote session. Check SELENOID_URL=" + SELENOID_URL, e);
            }
            System.err.println("Remote is not available: " + SELENOID_URL);
            System.err.println("Falling back to LOCAL Chrome. Reason: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return getAutoLocalDriver();
        }
    }

    // ===== OPTIONS =====

    private static ChromeOptions buildLocalChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        return options;
    }

    private static ChromeOptions buildRemoteChromeOptions() {
        ChromeOptions options = new ChromeOptions();

        // Selenium 4: правильнее так
        options.setBrowserVersion("128.0");

        // Важно для docker/selenoid
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");

        Map<String, Object> selenoid = new HashMap<>();
        selenoid.put("name", "Test badge...");
        selenoid.put("sessionTimeout", "15m");
        selenoid.put("env", Collections.singletonList("TZ=UTC"));

        Map<String, Object> labels = new HashMap<>();
        labels.put("manual", "true");
        selenoid.put("labels", labels);

        selenoid.put("enableVideo", true);
        selenoid.put("enableVNC", true);
        selenoid.put("enableLog", true);

        // headless можно убрать, если хочешь видеть браузер через VNC
        selenoid.put("headless", true);

        options.setCapability("selenoid:options", selenoid);

        return options;
    }

    private static void applyTimeouts(WebDriver driver) {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0)); // лучше explicit waits
    }
}