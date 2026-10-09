package tests.config;

import framework.config.ConfigManager;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ConfigManagerTest {

    @Test
    public void testDefaultConfigurationResolution() {
        Assert.assertNotNull(ConfigManager.getBrowser(), "Browser should not be null");
        Assert.assertNotNull(ConfigManager.getEnvironment(), "Environment should not be null");
        Assert.assertNotNull(ConfigManager.getBaseUrl(), "Base URL should not be null");
        Assert.assertTrue(ConfigManager.getTimeout() > 0, "Timeout should be greater than zero");
    }

    @Test
    public void testCliPropertyOverride() {
        // Simulate CLI override system property
        System.setProperty("browser", "firefox");
        Assert.assertEquals(ConfigManager.getBrowser(), "firefox", "CLI override should take precedence");

        // Clear override
        System.clearProperty("browser");
    }
}
