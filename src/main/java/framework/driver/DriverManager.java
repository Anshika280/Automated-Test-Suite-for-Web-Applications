package framework.driver;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DriverManager {

    private static final Logger log = LoggerFactory.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private DriverManager() {
        // Prevent instantiation
    }

    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    public static void setDriver(WebDriver driver) {
        if (driver != null) {
            log.debug("Binding WebDriver instance [{}] to thread [{}]", driver.hashCode(), Thread.currentThread().getName());
            driverThreadLocal.set(driver);
        }
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                log.debug("Quitting WebDriver instance [{}] on thread [{}]", driver.hashCode(), Thread.currentThread().getName());
                driver.quit();
            } catch (Exception e) {
                log.error("Error occurred while quitting WebDriver instance: {}", e.getMessage());
            } finally {
                driverThreadLocal.remove();
                log.debug("Removed ThreadLocal WebDriver reference for thread [{}]", Thread.currentThread().getName());
            }
        }
    }
}
