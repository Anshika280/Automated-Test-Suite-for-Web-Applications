package tests.utils;

import framework.base.BaseTest;
import framework.driver.DriverManager;
import framework.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WaitUtilsTest extends BaseTest {

    @Test
    public void testExplicitWaitUtilities() {
        WaitUtils waitUtils = new WaitUtils(DriverManager.getDriver(), 5);

        // Verify URL contains wait
        boolean urlContains = waitUtils.waitForUrlContains("saucedemo");
        Assert.assertTrue(urlContains, "URL should contain 'saucedemo'");

        // Verify visibility wait
        By usernameInput = By.id("user-name");
        WebElement element = waitUtils.waitForVisibility(usernameInput);
        Assert.assertNotNull(element, "Username input element should be visible");

        // Verify clickability wait
        By loginButton = By.id("login-button");
        WebElement button = waitUtils.waitForClickability(loginButton);
        Assert.assertNotNull(button, "Login button should be clickable");
    }
}
