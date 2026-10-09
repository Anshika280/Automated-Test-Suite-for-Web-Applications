package framework.config;

import framework.constants.FrameworkConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigManager {

    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);
    private static final Properties baseProperties = new Properties();
    private static final Properties envProperties = new Properties();

    static {
        loadBaseProperties();
        loadEnvProperties();
    }

    private ConfigManager() {
        // Prevent instantiation
    }

    private static void loadBaseProperties() {
        try (InputStream input = new FileInputStream(FrameworkConstants.CONFIG_FILE_PATH)) {
            baseProperties.load(input);
            log.info("Loaded base configuration from {}", FrameworkConstants.CONFIG_FILE_PATH);
        } catch (IOException e) {
            log.error("Failed to load base configuration file: {}", FrameworkConstants.CONFIG_FILE_PATH, e);
            throw new RuntimeException("Could not load base properties file", e);
        }
    }

    private static void loadEnvProperties() {
        String activeEnv = getProperty(FrameworkConstants.ENVIRONMENT_KEY, FrameworkConstants.DEFAULT_ENVIRONMENT);
        String envFilePath = FrameworkConstants.CONFIG_DIR_PATH + activeEnv.toLowerCase() + ".properties";

        try (InputStream input = new FileInputStream(envFilePath)) {
            envProperties.load(input);
            log.info("Loaded environment properties for environment [{}] from {}", activeEnv, envFilePath);
        } catch (IOException e) {
            log.warn("Environment configuration file [{}] not found. Falling back to default properties.", envFilePath);
        }
    }

    /**
     * Resolves property using 3-tier hierarchy:
     * 1. System property (-Dkey=value)
     * 2. Environment property (<env>.properties)
     * 3. Base property (config.properties)
     */
    public static String getProperty(String key) {
        if (key == null || key.trim().isEmpty()) {
            return null;
        }

        // Tier 1: System Property Override (-Dkey=value)
        String systemProp = System.getProperty(key);
        if (systemProp != null && !systemProp.trim().isEmpty()) {
            return systemProp.trim();
        }

        // Tier 1b: System Environment Variable (e.g. GRID_URL, BROWSER, HEADLESS, BASE_URL)
        String envVar = System.getenv(key);
        if (envVar == null) {
            String snakeCaseKey = key.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase().replace('.', '_');
            envVar = System.getenv(snakeCaseKey);
        }
        if (envVar == null) {
            envVar = System.getenv(key.toUpperCase().replace('.', '_'));
        }
        if (envVar != null && !envVar.trim().isEmpty()) {
            return envVar.trim();
        }

        // Tier 2: Environment-specific property
        String envProp = envProperties.getProperty(key);
        if (envProp != null && !envProp.trim().isEmpty()) {
            return envProp.trim();
        }

        // Tier 3: Base config property
        String baseProp = baseProperties.getProperty(key);
        if (baseProp != null && !baseProp.trim().isEmpty()) {
            return baseProp.trim();
        }

        return null;
    }

    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    public static String get(String key) {
        return getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        return getProperty(key, defaultValue);
    }

    public static String getBrowser() {
        return getProperty(FrameworkConstants.BROWSER_KEY, FrameworkConstants.DEFAULT_BROWSER);
    }

    public static String getEnvironment() {
        return getProperty(FrameworkConstants.ENVIRONMENT_KEY, FrameworkConstants.DEFAULT_ENVIRONMENT);
    }

    public static String getBaseUrl() {
        return getProperty(FrameworkConstants.BASE_URL_KEY);
    }

    public static String getApiUrl() {
        return getProperty(FrameworkConstants.API_URL_KEY);
    }

    public static int getTimeout() {
        String timeoutStr = getProperty(FrameworkConstants.TIMEOUT_KEY);
        if (timeoutStr != null) {
            try {
                return Integer.parseInt(timeoutStr);
            } catch (NumberFormatException e) {
                log.warn("Invalid timeout value '{}' in properties. Using default timeout: {}", timeoutStr, FrameworkConstants.DEFAULT_TIMEOUT);
            }
        }
        return FrameworkConstants.DEFAULT_TIMEOUT;
    }

    public static boolean isHeadless() {
        String headlessStr = getProperty(FrameworkConstants.HEADLESS_KEY, "false");
        return Boolean.parseBoolean(headlessStr);
    }

    public static boolean isScreenshotOnFailure() {
        String screenshotStr = getProperty(FrameworkConstants.SCREENSHOT_ON_FAILURE_KEY, "true");
        return Boolean.parseBoolean(screenshotStr);
    }

    public static String getGridUrl() {
        return getProperty(FrameworkConstants.GRID_URL_KEY);
    }
}
