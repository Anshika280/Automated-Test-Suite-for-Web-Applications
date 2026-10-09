package tests.ui;

import framework.base.BaseTest;
import framework.driver.DriverManager;
import org.testng.Assert;
import org.testng.annotations.Test;

public class BaseTestSampleTest extends BaseTest {

    @Test
    public void testBaseTestLifecycleAndNavigation() {
        Assert.assertNotNull(DriverManager.getDriver(), "Driver initialized in BaseTest should not be null");
        String currentUrl = DriverManager.getDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("saucedemo"), "Current URL should contain 'saucedemo', but was: " + currentUrl);
    }
}
