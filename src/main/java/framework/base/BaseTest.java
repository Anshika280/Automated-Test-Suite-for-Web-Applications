package framework.base;

import framework.config.ConfigManager;
import framework.driver.DriverFactory;
import framework.driver.DriverManager;
import framework.utils.ScreenshotUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.lang.reflect.Method;

public abstract class BaseTest {

    protected static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        log.info("=================================================================");
        log.info("Starting Test Suite Execution");
        log.info("Active Environment: [{}] | Base URL: [{}]", ConfigManager.getEnvironment(), ConfigManager.getBaseUrl());
        log.info("=================================================================");
    }

    @BeforeTest(alwaysRun = true)
    public void beforeTest() {
        log.debug("Executing @BeforeTest setup");
    }

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser", "headless", "gridUrl"})
    public void setUp(Method method, @Optional String browser, @Optional String headless, @Optional String gridUrl) {
        String testName = method.getName();
        log.info("--- Starting Test Execution: [{}] ---", testName);

        String targetBrowser = (browser != null && !browser.isEmpty()) ? browser : ConfigManager.getBrowser();
        boolean isHeadless = (headless != null && !headless.isEmpty()) ? Boolean.parseBoolean(headless) : ConfigManager.isHeadless();
        String targetGridUrl = (gridUrl != null && !gridUrl.isEmpty()) ? gridUrl : ConfigManager.getGridUrl();

        WebDriver driver = DriverFactory.createDriver(targetBrowser, isHeadless, targetGridUrl);
        DriverManager.setDriver(driver);

        String baseUrl = ConfigManager.getBaseUrl();
        if (baseUrl != null && !baseUrl.isEmpty()) {
            log.info("Navigating to base URL: [{}]", baseUrl);
            DriverManager.getDriver().get(baseUrl);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        String testName = result.getName();

        if (result.getStatus() == ITestResult.FAILURE) {
            log.error("Test FAILED: [{}]", testName);
            if (result.getThrowable() != null) {
                log.error("Failure Exception: ", result.getThrowable());
            }
            if (ConfigManager.isScreenshotOnFailure()) {
                ScreenshotUtils.captureScreenshot(testName);
            }
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            log.info("Test PASSED: [{}]", testName);
        } else if (result.getStatus() == ITestResult.SKIP) {
            log.warn("Test SKIPPED: [{}]", testName);
        }

        log.info("Cleaning up WebDriver for Test: [{}]", testName);
        DriverManager.quitDriver();
        log.info("--- Finished Test Execution: [{}] ---\n", testName);
    }

    @AfterTest(alwaysRun = true)
    public void afterTest() {
        log.debug("Executing @AfterTest teardown");
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        log.info("=================================================================");
        log.info("Finished Test Suite Execution");
        log.info("=================================================================");
    }
}
