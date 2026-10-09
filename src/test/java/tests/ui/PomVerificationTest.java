package tests.ui;

import framework.base.BaseTest;
import framework.driver.DriverManager;
import framework.pages.CartPage;
import framework.pages.InventoryPage;
import framework.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PomVerificationTest extends BaseTest {

    @Test
    public void testValidLoginAndCartWorkflowUsingPOM() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());

        // Fluid Page Object pattern
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        Assert.assertTrue(inventoryPage.isPageLoaded(), "Inventory page should be loaded after valid login");

        inventoryPage.addBackpackToCart();
        Assert.assertEquals(inventoryPage.getCartItemBadgeCount(), "1", "Cart item count badge should display '1'");

        CartPage cartPage = inventoryPage.clickShoppingCart();
        Assert.assertTrue(cartPage.isPageLoaded(), "Cart page should be loaded");
        Assert.assertEquals(cartPage.getFirstItemName(), "Sauce Labs Backpack", "Cart should contain 'Sauce Labs Backpack'");
    }

    @Test
    public void testInvalidLoginErrorMessageUsingPOM() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());

        loginPage.enterUsername("invalid_user")
                 .enterPassword("wrong_password")
                 .clickLoginExpectingFailure();

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed for invalid credentials");
        Assert.assertTrue(loginPage.getErrorMessageText().contains("Username and password do not match"),"Error message text was unexpected: " + loginPage.getErrorMessageText());
    }
}
