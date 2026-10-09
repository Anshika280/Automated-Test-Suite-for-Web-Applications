package tests.ui;

import framework.base.BaseTest;
import framework.driver.DriverManager;
import framework.pages.InventoryPage;
import framework.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SmokeTest extends BaseTest {

    @Test(groups = {"smoke"})
    public void testApplicationLoad() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        Assert.assertTrue(loginPage.getCurrentUrl().contains("saucedemo.com"), "Application should load successfully");
        Assert.assertEquals(loginPage.getPageTitle(), "Swag Labs", "Page title should match");
    }

    @Test(groups = {"smoke"})
    public void testValidLoginAndLogout() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        Assert.assertTrue(inventoryPage.isPageLoaded(), "Inventory page should be visible post-login");

        LoginPage postLogoutPage = inventoryPage.logout();
        Assert.assertTrue(postLogoutPage.getCurrentUrl().contains("saucedemo.com"), "Should return to login URL post-logout");
    }

    @Test(groups = {"smoke"})
    public void testMainDashboardLoads() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        Assert.assertEquals(inventoryPage.getPageTitleText(), "Products", "Dashboard header should display 'Products'");
    }
}
