package tests.driver;

import framework.driver.DriverFactory;
import framework.driver.DriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class DriverFactoryTest {

    @AfterMethod
    public void tearDown() {
        DriverManager.quitDriver();
    }

    @Test(priority = 1)
    public void testHeadlessChromeDriverInitialization() {
        String gridUrl = framework.config.ConfigManager.getGridUrl();
        WebDriver driver = DriverFactory.createDriver("chrome", true, gridUrl);
        DriverManager.setDriver(driver);

        Assert.assertNotNull(DriverManager.getDriver(), "ThreadLocal driver should not be null");
        driver.get(framework.config.ConfigManager.getBaseUrl() != null ? framework.config.ConfigManager.getBaseUrl() : "https://www.saucedemo.com");
        Assert.assertNotNull(driver.getTitle(), "Page title should not be null");
    }

    @Test(priority = 2)
    public void testBrowserOptionsConfiguration() {
        ChromeOptions chromeOptions = DriverFactory.getChromeOptions(true);
        Assert.assertNotNull(chromeOptions, "ChromeOptions should not be null");
        Assert.assertEquals(chromeOptions.getBrowserName(), "chrome", "Browser name should be chrome");

        FirefoxOptions firefoxOptions = DriverFactory.getFirefoxOptions(true);
        Assert.assertNotNull(firefoxOptions, "FirefoxOptions should not be null");
        Assert.assertEquals(firefoxOptions.getBrowserName(), "firefox", "Browser name should be firefox");

        EdgeOptions edgeOptions = DriverFactory.getEdgeOptions(true);
        Assert.assertNotNull(edgeOptions, "EdgeOptions should not be null");
        Assert.assertEquals(edgeOptions.getBrowserName(), "MicrosoftEdge", "Browser name should be MicrosoftEdge");
    }

    @Test(priority = 3)
    public void testRemoteDriverSelectionAttempt() {
        String invalidGridUrl = "http://invalid-grid-host-9999:4444";
        try {
            DriverFactory.createDriver("chrome", true, invalidGridUrl);
            Assert.fail("Should have thrown Exception when attempting RemoteWebDriver on unreachable grid");
        } catch (RuntimeException e) {
            Assert.assertTrue(e.getMessage().contains("Selenium Grid") || e.getMessage().contains("RemoteWebDriver"),
                    "Exception message should mention Selenium Grid or RemoteWebDriver attempt");
        }
    }
}
