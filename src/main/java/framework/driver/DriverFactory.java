package framework.driver;

import framework.config.ConfigManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URL;

/**
 * Thread-safe WebDriver Factory supporting local and remote (Selenium Grid) browser session creation.
 */
public final class DriverFactory {

    private static final Logger log = LoggerFactory.getLogger(DriverFactory.class);

    private DriverFactory() {
        // Prevent instantiation
    }

    public static WebDriver createDriver(String browser, boolean headless) {
        return createDriver(browser, headless, ConfigManager.getGridUrl());
    }

    public static WebDriver createDriver(String browser, boolean headless, String gridUrl) {
        String targetBrowser = (browser != null && !browser.trim().isEmpty())
                ? browser.toLowerCase().trim()
                : ConfigManager.getBrowser().toLowerCase();

        boolean isRemote = (gridUrl != null && !gridUrl.trim().isEmpty());
        WebDriver driver;

        if (isRemote) {
            log.info("Initializing RemoteWebDriver for browser [{}] on Grid Hub [{}] (Headless: {})", targetBrowser, gridUrl, headless);
            try {
                URL hubUrl = URI.create(gridUrl.trim()).toURL();
                switch (targetBrowser) {
                    case "firefox":
                        driver = new RemoteWebDriver(hubUrl, getFirefoxOptions(headless));
                        break;
                    case "edge":
                        driver = new RemoteWebDriver(hubUrl, getEdgeOptions(headless));
                        break;
                    case "chrome":
                    default:
                        driver = new RemoteWebDriver(hubUrl, getChromeOptions(headless));
                        break;
                }
            } catch (Exception e) {
                log.error("Failed to create RemoteWebDriver session on Grid Hub [{}]: {}", gridUrl, e.getMessage(), e);
                throw new RuntimeException("Could not create RemoteWebDriver session on Selenium Grid", e);
            }
        } else {
            log.info("Initializing Local WebDriver for browser [{}] (Headless: {})", targetBrowser, headless);
            switch (targetBrowser) {
                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    driver = new FirefoxDriver(getFirefoxOptions(headless));
                    break;
                case "edge":
                    WebDriverManager.edgedriver().setup();
                    driver = new EdgeDriver(getEdgeOptions(headless));
                    break;
                case "chrome":
                default:
                    if (!"chrome".equals(targetBrowser)) {
                        log.warn("Unsupported browser '{}' requested. Defaulting to Chrome.", targetBrowser);
                    }
                    WebDriverManager.chromedriver().setup();
                    driver = new ChromeDriver(getChromeOptions(headless));
                    break;
            }
        }

        try {
            driver.manage().window().maximize();
        } catch (Exception e) {
            log.warn("Could not maximize browser window: {}", e.getMessage());
        }

        return driver;
    }

    public static ChromeOptions getChromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
        }
        return options;
    }

    public static FirefoxOptions getFirefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
            options.addArguments("--width=1920");
            options.addArguments("--height=1080");
        }
        return options;
    }

    public static EdgeOptions getEdgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
        }
        return options;
    }
}
