package tests.ui;

import framework.base.BaseTest;
import framework.driver.DriverManager;
import framework.pages.CartPage;
import framework.pages.CheckoutPage;
import framework.pages.InventoryPage;
import framework.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RegressionTest extends BaseTest {

    @Test(groups = {"regression"})
    public void testFullRegressionUserJourney() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());

        // Step 1: Login
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        Assert.assertTrue(inventoryPage.isPageLoaded(), "Inventory page must load after login");

        // Step 2: Add item to cart
        inventoryPage.addBackpackToCart();
        Assert.assertEquals(inventoryPage.getCartItemBadgeCount(), "1", "Cart badge count must equal 1");

        // Step 3: View cart
        CartPage cartPage = inventoryPage.clickShoppingCart();
        Assert.assertTrue(cartPage.isPageLoaded(), "Cart page must load");
        Assert.assertEquals(cartPage.getFirstItemName(), "Sauce Labs Backpack", "Product name in cart must match");

        // Step 4: Complete checkout
        cartPage.clickCheckout();
        CheckoutPage checkoutPage = new CheckoutPage(DriverManager.getDriver());
        checkoutPage.enterInformation("Alex", "Smith", "10001")
                    .clickContinue()
                    .clickFinish();

        Assert.assertTrue(checkoutPage.isOrderComplete(), "Order completion header must confirm order placement");
    }
}
