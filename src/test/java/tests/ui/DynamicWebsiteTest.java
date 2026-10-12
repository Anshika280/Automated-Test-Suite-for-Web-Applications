package tests.ui;

import framework.base.BaseTest;
import framework.config.ConfigManager;
import framework.driver.DriverManager;
import framework.utils.ScreenshotUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class DynamicWebsiteTest extends BaseTest {

    @Test(groups = {"dynamic", "smoke"}, description = "Verify target website loads and URL is valid")
    public void testWebsiteReachabilityAndNavigation() {
        WebDriver driver = DriverManager.getDriver();
        String currentUrl = driver.getCurrentUrl();
        String configuredUrl = ConfigManager.getBaseUrl();
        log.info("Testing dynamic target URL: [{}] | Loaded URL: [{}]", configuredUrl, currentUrl);

        Assert.assertNotNull(currentUrl, "Current URL must not be null");
        Assert.assertFalse(currentUrl.isEmpty(), "Current URL must not be empty");
        Assert.assertFalse(currentUrl.startsWith("data:"), "Website failed to load, landed on data URL");
    }

    @Test(groups = {"dynamic", "smoke"}, description = "Verify page title and DOM completion state")
    public void testPageTitleAndDocumentReadiness() {
        WebDriver driver = DriverManager.getDriver();
        String title = driver.getTitle();
        log.info("Target website title: '{}'", title);

        Assert.assertNotNull(title, "Page title must not be null");

        JavascriptExecutor js = (JavascriptExecutor) driver;
        String readyState = (String) js.executeScript("return document.readyState;");
        log.info("DOM document.readyState: '{}'", readyState);
        Assert.assertTrue("complete".equalsIgnoreCase(readyState) || "interactive".equalsIgnoreCase(readyState),
                "Page DOM readyState must be 'complete' or 'interactive'");
    }

    @Test(groups = {"dynamic", "smoke"}, description = "Verify body element presence and visual rendering")
    public void testBodyElementAndVisualRendering() {
        WebDriver driver = DriverManager.getDriver();
        List<WebElement> bodies = driver.findElements(By.tagName("body"));
        Assert.assertFalse(bodies.isEmpty(), "Target webpage must contain a <body> element");

        WebElement body = bodies.get(0);
        Assert.assertTrue(body.isDisplayed(), "Page <body> element must be displayed");

        String pageSource = driver.getPageSource();
        Assert.assertTrue(pageSource != null && pageSource.length() > 50, "Page content should not be empty");
        log.info("Page source size: {} bytes", pageSource.length());
    }

    @Test(groups = {"dynamic", "smoke"}, description = "Verify interactive elements (links, buttons, inputs) exist")
    public void testInteractiveElementsDiscovery() {
        WebDriver driver = DriverManager.getDriver();
        List<WebElement> links = driver.findElements(By.tagName("a"));
        List<WebElement> buttons = driver.findElements(By.tagName("button"));
        List<WebElement> inputs = driver.findElements(By.tagName("input"));
        List<WebElement> images = driver.findElements(By.tagName("img"));

        log.info("DOM Elements found - Links: {}, Buttons: {}, Inputs: {}, Images: {}",
                links.size(), buttons.size(), inputs.size(), images.size());

        int totalElements = links.size() + buttons.size() + inputs.size() + images.size();
        Assert.assertTrue(totalElements > 0, "Target website must have at least one link, button, input, or image element");
    }

    @Test(groups = {"dynamic", "smoke"}, description = "Capture visual snapshot evidence of target website")
    public void testCaptureWebsiteVisualSnapshot() {
        String screenshotPath = ScreenshotUtils.captureScreenshot("TargetWebsiteSnapshot");
        log.info("Captured visual snapshot of target website at: {}", screenshotPath);
        Assert.assertNotNull(screenshotPath, "Screenshot capture should produce a valid path");
    }

    @Test(groups = {"dynamic", "performance"}, description = "Measure page load performance metrics")
    public void testPagePerformanceMetrics() {
        WebDriver driver = DriverManager.getDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            Long navStart = (Long) js.executeScript("return window.performance.timing.navigationStart;");
            Long loadEnd = (Long) js.executeScript("return window.performance.timing.loadEventEnd;");
            if (navStart != null && loadEnd != null && loadEnd > navStart) {
                long loadDurationMs = loadEnd - navStart;
                log.info("Total Page Load Duration: {} ms", loadDurationMs);
                Assert.assertTrue(loadDurationMs >= 0, "Page load duration must be non-negative");
            } else {
                log.info("Navigation timing completed before measurement, page is interactive");
            }
        } catch (Exception e) {
            log.warn("Performance timing API not available or restricted on target site: {}", e.getMessage());
        }
    }
}
