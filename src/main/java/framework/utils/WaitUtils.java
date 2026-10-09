package framework.utils;

import framework.config.ConfigManager;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

public class WaitUtils {

    private static final Logger log = LoggerFactory.getLogger(WaitUtils.class);
    private final WebDriver driver;
    private final int defaultTimeoutSeconds;

    public WaitUtils(WebDriver driver) {
        this.driver = driver;
        this.defaultTimeoutSeconds = ConfigManager.getTimeout();
    }

    public WaitUtils(WebDriver driver, int customTimeoutSeconds) {
        this.driver = driver;
        this.defaultTimeoutSeconds = customTimeoutSeconds;
    }

    private WebDriverWait getWait(int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }

    public WebElement waitForVisibility(By locator) {
        return waitForVisibility(locator, defaultTimeoutSeconds);
    }

    public WebElement waitForVisibility(By locator, int timeoutSeconds) {
        log.debug("Waiting max {}s for visibility of element [{}]", timeoutSeconds, locator);
        return getWait(timeoutSeconds).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public List<WebElement> waitForVisibilityOfAll(By locator) {
        return waitForVisibilityOfAll(locator, defaultTimeoutSeconds);
    }

    public List<WebElement> waitForVisibilityOfAll(By locator, int timeoutSeconds) {
        log.debug("Waiting max {}s for visibility of all elements located by [{}]", timeoutSeconds, locator);
        return getWait(timeoutSeconds).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public WebElement waitForClickability(By locator) {
        return waitForClickability(locator, defaultTimeoutSeconds);
    }

    public WebElement waitForClickability(By locator, int timeoutSeconds) {
        log.debug("Waiting max {}s for element to be clickable [{}]", timeoutSeconds, locator);
        return getWait(timeoutSeconds).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement waitForPresence(By locator) {
        return waitForPresence(locator, defaultTimeoutSeconds);
    }

    public WebElement waitForPresence(By locator, int timeoutSeconds) {
        log.debug("Waiting max {}s for presence of element [{}]", timeoutSeconds, locator);
        return getWait(timeoutSeconds).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public boolean waitForInvisibility(By locator) {
        return waitForInvisibility(locator, defaultTimeoutSeconds);
    }

    public boolean waitForInvisibility(By locator, int timeoutSeconds) {
        log.debug("Waiting max {}s for element to disappear [{}]", timeoutSeconds, locator);
        return getWait(timeoutSeconds).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public boolean waitForUrlToBe(String expectedUrl) {
        return waitForUrlToBe(expectedUrl, defaultTimeoutSeconds);
    }

    public boolean waitForUrlToBe(String expectedUrl, int timeoutSeconds) {
        log.debug("Waiting max {}s for URL to be [{}]", timeoutSeconds, expectedUrl);
        return getWait(timeoutSeconds).until(ExpectedConditions.urlToBe(expectedUrl));
    }

    public boolean waitForUrlContains(String fraction) {
        return waitForUrlContains(fraction, defaultTimeoutSeconds);
    }

    public boolean waitForUrlContains(String fraction, int timeoutSeconds) {
        log.debug("Waiting max {}s for URL to contain [{}]", timeoutSeconds, fraction);
        return getWait(timeoutSeconds).until(ExpectedConditions.urlContains(fraction));
    }

    public boolean waitForTextToBePresentInElement(By locator, String expectedText) {
        return waitForTextToBePresentInElement(locator, expectedText, defaultTimeoutSeconds);
    }

    public boolean waitForTextToBePresentInElement(By locator, String expectedText, int timeoutSeconds) {
        log.debug("Waiting max {}s for element [{}] to present text '{}'", timeoutSeconds, locator, expectedText);
        return getWait(timeoutSeconds).until(ExpectedConditions.textToBePresentInElementLocated(locator, expectedText));
    }

    public boolean waitForAttributeToBe(By locator, String attribute, String expectedValue) {
        return waitForAttributeToBe(locator, attribute, expectedValue, defaultTimeoutSeconds);
    }

    public boolean waitForAttributeToBe(By locator, String attribute, String expectedValue, int timeoutSeconds) {
        log.debug("Waiting max {}s for element [{}] attribute '{}' to be '{}'", timeoutSeconds, locator, attribute, expectedValue);
        return getWait(timeoutSeconds).until(ExpectedConditions.attributeToBe(locator, attribute, expectedValue));
    }

    public WebDriver waitForFrameAndSwitchToIt(By frameLocator) {
        return waitForFrameAndSwitchToIt(frameLocator, defaultTimeoutSeconds);
    }

    public WebDriver waitForFrameAndSwitchToIt(By frameLocator, int timeoutSeconds) {
        log.debug("Waiting max {}s for frame [{}] and switching to it", timeoutSeconds, frameLocator);
        return getWait(timeoutSeconds).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameLocator));
    }

    public Alert waitForAlert() {
        return waitForAlert(defaultTimeoutSeconds);
    }

    public Alert waitForAlert(int timeoutSeconds) {
        log.debug("Waiting max {}s for alert to be present", timeoutSeconds);
        return getWait(timeoutSeconds).until(ExpectedConditions.alertIsPresent());
    }

    public <T> T waitUntilCondition(ExpectedCondition<T> condition, int timeoutSeconds) {
        log.debug("Waiting for custom condition with timeout {}s", timeoutSeconds);
        return getWait(timeoutSeconds).until(condition);
    }
}
