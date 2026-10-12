package framework.pages;

import framework.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasePage {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    protected final WebDriver driver;
    protected final WaitUtils waitUtils;

    public BasePage(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver instance passed to BasePage cannot be null");
        }
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }

    protected void click(By locator) {
        log.info("Clicking element [{}]", locator);
        WebElement element = waitUtils.waitForClickability(locator);
        try {
            element.click();
        } catch (Exception e) {
            log.warn("Standard click failed for element [{}], executing JavaScript click fallback...", locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    protected void click(By locator, int customTimeoutSeconds) {
        log.info("Clicking element [{}] with custom timeout {}s", locator, customTimeoutSeconds);
        WebElement element = waitUtils.waitForClickability(locator, customTimeoutSeconds);
        try {
            element.click();
        } catch (Exception e) {
            log.warn("Standard click failed for element [{}], executing JavaScript click fallback...", locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    protected void sendKeys(By locator, String text) {
        log.info("Entering text into element [{}]: '{}'", locator, text);
        WebElement element = waitUtils.waitForVisibility(locator);
        element.click();
        element.clear();
        element.sendKeys(text);
        if (text != null && !text.isEmpty()) {
            String entered = element.getAttribute("value");
            if (!text.equals(entered)) {
                log.warn("Text mismatch on [{}] after sendKeys. Expected: '{}', Found: '{}'. Retrying with JS setter...", locator, text, entered);
                element.click();
                ((JavascriptExecutor) driver).executeScript(
                    "var el = arguments[0];" +
                    "var setter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value');" +
                    "if (setter && setter.set) { setter.set.call(el, arguments[1]); }" +
                    "else { el.value = arguments[1]; }" +
                    "el.dispatchEvent(new Event('input', { bubbles: true }));" +
                    "el.dispatchEvent(new Event('change', { bubbles: true }));",
                    element, text
                );
            }
        }
    }

    protected void sendKeys(By locator, String text, int customTimeoutSeconds) {
        log.info("Entering text into element [{}]: '{}' with custom timeout {}s", locator, text, customTimeoutSeconds);
        WebElement element = waitUtils.waitForVisibility(locator, customTimeoutSeconds);
        element.click();
        element.clear();
        element.sendKeys(text);
        if (text != null && !text.isEmpty()) {
            String entered = element.getAttribute("value");
            if (!text.equals(entered)) {
                log.warn("Text mismatch on [{}] after sendKeys. Expected: '{}', Found: '{}'. Retrying with JS setter...", locator, text, entered);
                element.click();
                ((JavascriptExecutor) driver).executeScript(
                    "var el = arguments[0];" +
                    "var setter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value');" +
                    "if (setter && setter.set) { setter.set.call(el, arguments[1]); }" +
                    "else { el.value = arguments[1]; }" +
                    "el.dispatchEvent(new Event('input', { bubbles: true }));" +
                    "el.dispatchEvent(new Event('change', { bubbles: true }));",
                    element, text
                );
            }
        }
    }

    protected String getText(By locator) {
        WebElement element = waitUtils.waitForVisibility(locator);
        String text = element.getText();
        log.info("Retrieved text from element [{}]: '{}'", locator, text);
        return text;
    }

    protected boolean isDisplayed(By locator) {
        try {
            WebElement element = waitUtils.waitForVisibility(locator);
            return element.isDisplayed();
        } catch (Exception e) {
            log.debug("Element [{}] is not displayed: {}", locator, e.getMessage());
            return false;
        }
    }

    protected boolean isDisplayed(By locator, int customTimeoutSeconds) {
        try {
            WebElement element = waitUtils.waitForVisibility(locator, customTimeoutSeconds);
            return element.isDisplayed();
        } catch (Exception e) {
            log.debug("Element [{}] is not displayed within {}s: {}", locator, customTimeoutSeconds, e.getMessage());
            return false;
        }
    }

    public String getPageTitle() {
        String title = driver.getTitle();
        log.info("Current Page Title: '{}'", title);
        return title;
    }

    public String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        log.info("Current Page URL: '{}'", url);
        return url;
    }
}
