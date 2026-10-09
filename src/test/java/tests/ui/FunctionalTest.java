package tests.ui;

import framework.base.BaseTest;
import framework.driver.DriverManager;
import framework.pages.CartPage;
import framework.pages.CheckoutPage;
import framework.pages.InventoryPage;
import framework.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FunctionalTest extends BaseTest {

    @Test(groups = {"functional"})
    public void testLockedOutUserLoginFailure() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        loginPage.login("locked_out_user", "secret_sauce");

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be visible for locked out user");
        Assert.assertTrue(loginPage.getErrorMessageText().contains("Sorry, this user has been locked out"),
                "Error message should mention user locked out");
    }

    @Test(groups = {"functional"})
    public void testEmptyFieldsValidation() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        loginPage.clickLoginExpectingFailure();

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Validation error should appear for empty fields");
        Assert.assertTrue(loginPage.getErrorMessageText().contains("Username is required"),
                "Error message should indicate username is required");
    }

    @Test(groups = {"functional"})
    public void testAddItemToCartAndRemove() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");

        inventoryPage.addBackpackToCart();
        Assert.assertEquals(inventoryPage.getCartItemBadgeCount(), "1", "Cart badge count should be 1");

        inventoryPage.removeBackpackFromCart();
        Assert.assertFalse(inventoryPage.isCartBadgeDisplayed(), "Cart badge should not be displayed after removing item");
    }

    @Test(groups = {"functional"})
    public void testEndToEndCheckoutWorkflow() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");

        inventoryPage.addBackpackToCart();
        CartPage cartPage = inventoryPage.clickShoppingCart();
        Assert.assertTrue(cartPage.isPageLoaded(), "Cart page should be loaded");
        Assert.assertEquals(cartPage.getFirstItemName(), "Sauce Labs Backpack", "Correct product should be in cart");

        cartPage.clickCheckout();
        CheckoutPage checkoutPage = new CheckoutPage(DriverManager.getDriver());
        checkoutPage.enterInformation("Jane", "Doe", "90210")
                    .clickContinue()
                    .clickFinish();

        Assert.assertTrue(checkoutPage.isOrderComplete(), "Order completion header should confirm purchase");
    }
}
