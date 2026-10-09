package framework.utils;

import framework.constants.FrameworkConstants;
import framework.driver.DriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class ScreenshotUtils {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotUtils.class);

    private ScreenshotUtils() {
        // Prevent instantiation
    }

    public static String captureScreenshot(String testName) {
        WebDriver driver = DriverManager.getDriver();
        if (driver == null) {
            log.warn("Cannot capture screenshot for test '{}': WebDriver instance is null", testName);
            return null;
        }

        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            byte[] screenshotBytes = ts.getScreenshotAs(OutputType.BYTES);

            File screenshotsDir = new File(FrameworkConstants.SCREENSHOTS_PATH);
            if (!screenshotsDir.exists()) {
                boolean created = screenshotsDir.mkdirs();
                if (created) {
                    log.info("Created screenshots directory at {}", FrameworkConstants.SCREENSHOTS_PATH);
                }
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
            String fileName = testName + "_" + timestamp + ".png";
            String destinationPath = FrameworkConstants.SCREENSHOTS_PATH + File.separator + fileName;

            try (FileOutputStream fos = new FileOutputStream(destinationPath)) {
                fos.write(screenshotBytes);
                log.info("Saved failure screenshot for test '{}' at {}", testName, destinationPath);
            }

            return destinationPath;
        } catch (IOException e) {
            log.error("Failed to save screenshot file for test '{}': {}", testName, e.getMessage(), e);
            return null;
        } catch (Exception e) {
            log.error("Unexpected error capturing screenshot for test '{}': {}", testName, e.getMessage(), e);
            return null;
        }
    }

    public static byte[] getScreenshotBytes() {
        WebDriver driver = DriverManager.getDriver();
        if (driver != null) {
            try {
                return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            } catch (Exception e) {
                log.error("Failed to capture screenshot bytes: {}", e.getMessage());
            }
        }
        return new byte[0];
    }
}
