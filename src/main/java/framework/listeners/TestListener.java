package framework.listeners;

import framework.driver.DriverManager;
import framework.utils.ScreenshotUtils;
import io.qameta.allure.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG Execution Listener that tracks test lifecycle events, logs execution status,
 * automatically captures screenshots on failures, and attaches images to Allure reports.
 */
public class TestListener implements ITestListener {

    private static final Logger log = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        log.info("=================================================================");
        log.info("Starting Test Suite Execution: [{}]", context.getName());
        log.info("=================================================================");
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("=================================================================");
        log.info("Finished Test Suite Execution: [{}]", context.getName());
        log.info("Passed Tests: {}", context.getPassedTests().size());
        log.info("Failed Tests: {}", context.getFailedTests().size());
        log.info("Skipped Tests: {}", context.getSkippedTests().size());
        log.info("=================================================================");
        framework.utils.SlackNotifier.sendExecutionSummary(context);
    }

    @Override
    public void onTestStart(ITestResult result) {
        log.info("---> Starting Test: [{}.{}]",
                result.getTestClass().getRealClass().getSimpleName(),
                result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        log.info("<--- Test PASSED: [{}.{}] (Duration: {} ms)",
                result.getTestClass().getRealClass().getSimpleName(),
                result.getMethod().getMethodName(),
                duration);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        String testName = result.getMethod().getMethodName();

        log.error("<--- Test FAILED: [{}.{}] (Duration: {} ms)",
                result.getTestClass().getRealClass().getSimpleName(),
                testName,
                duration);

        if (result.getThrowable() != null) {
            log.error("Failure Exception: ", result.getThrowable());
        }

        // Automatic screenshot capture if driver is active
        if (DriverManager.getDriver() != null) {
            log.info("Capturing failure screenshot for test [{}]", testName);
            ScreenshotUtils.captureScreenshot(testName);
            attachScreenshotToAllure(testName, ScreenshotUtils.getScreenshotBytes());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("<--- Test SKIPPED: [{}.{}]",
                result.getTestClass().getRealClass().getSimpleName(),
                result.getMethod().getMethodName());
        if (result.getThrowable() != null) {
            log.warn("Skip Reason: ", result.getThrowable());
        }
    }

    /**
     * Attaches screenshot byte array directly into Allure HTML test report.
     *
     * @param testName Test method name
     * @param screenshot Image bytes
     * @return Raw screenshot byte array for Allure attachment rendering
     */
    @Attachment(value = "Failure Screenshot - {0}", type = "image/png")
    public byte[] attachScreenshotToAllure(String testName, byte[] screenshot) {
        return screenshot;
    }
}
