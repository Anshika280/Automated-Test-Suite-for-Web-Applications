package framework.constants;

import java.io.File;

public final class FrameworkConstants {

    private FrameworkConstants() {
        // Prevent instantiation
    }

    // Paths
    public static final String USER_DIR = System.getProperty("user.dir");
    public static final String CONFIG_FILE_PATH = USER_DIR + File.separator + "src" + File.separator + "main"
            + File.separator + "resources" + File.separator + "config" + File.separator + "config.properties";
    public static final String CONFIG_DIR_PATH = USER_DIR + File.separator + "src" + File.separator + "main"
            + File.separator + "resources" + File.separator + "config" + File.separator;

    public static final String SCREENSHOTS_PATH = USER_DIR + File.separator + "screenshots";
    public static final String LOGS_PATH = USER_DIR + File.separator + "logs";
    public static final String REPORTS_PATH = USER_DIR + File.separator + "reports";

    // Property Keys
    public static final String BROWSER_KEY = "browser";
    public static final String ENVIRONMENT_KEY = "environment";
    public static final String BASE_URL_KEY = "baseUrl";
    public static final String API_URL_KEY = "apiUrl";
    public static final String TIMEOUT_KEY = "timeout";
    public static final String HEADLESS_KEY = "headless";
    public static final String SCREENSHOT_ON_FAILURE_KEY = "screenshotOnFailure";
    public static final String GRID_URL_KEY = "gridUrl";

    // Defaults
    public static final String DEFAULT_BROWSER = "chrome";
    public static final String DEFAULT_ENVIRONMENT = "qa";
    public static final int DEFAULT_TIMEOUT = 10;
}
