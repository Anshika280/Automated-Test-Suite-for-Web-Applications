package tests.integration;

import framework.api.AuthClient;
import framework.api.UserClient;
import framework.base.BaseTest;
import framework.driver.DriverManager;
import framework.models.UserRequestModel;
import framework.pages.CartPage;
import framework.pages.CheckoutPage;
import framework.pages.InventoryPage;
import framework.pages.LoginPage;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ApiUiIntegrationTest extends BaseTest {

    private AuthClient authClient;
    private UserClient userClient;
    private String apiAuthToken;
    private String seededUserName;

    @BeforeClass(alwaysRun = true)
    public void setupApiSeededData() {
        authClient = new AuthClient();
        userClient = new UserClient();

        // 1. API Data Seeding: Authenticate via API
        UserRequestModel authRequest = new UserRequestModel("eve.holt@reqres.in", "cityslicka", true);
        Response authResponse = authClient.login(authRequest);
        int statusCode = authResponse.getStatusCode();
        if (statusCode == 429) {
            throw new SkipException("External API ReqRes returned HTTP 429 (Rate Limit). Skipping integration test data setup.");
        }
        Assert.assertEquals(statusCode, 200, "API authentication should return HTTP 200 OK");
        apiAuthToken = authResponse.jsonPath().getString("token");
        Assert.assertNotNull(apiAuthToken, "Auth token in response should not be null");
        log.info("API Data Seeding Successful: Obtained Auth Token [{}]", apiAuthToken);

        // 2. API Data Seeding: Create user via API
        UserRequestModel userRequest = new UserRequestModel("Sarah Jenkins", "Senior SDET");
        Response userResponse = userClient.createUser(userRequest);
        int userStatus = userResponse.getStatusCode();
        if (userStatus == 429) {
            throw new SkipException("External API ReqRes returned HTTP 429 (Rate Limit). Skipping integration test data setup.");
        }
        Assert.assertEquals(userStatus, 201, "API user creation should return HTTP 201 Created");
        seededUserName = userResponse.jsonPath().getString("name");
        Assert.assertNotNull(seededUserName, "Created user name should not be null");
        log.info("API Data Seeding Successful: Created User [{}]", seededUserName);
    }

    @Test(groups = {"integration", "regression"})
    public void testApiSeededDataVerificationOnUi() {
        log.info("--- Starting API + UI Hybrid Integration Test ---");

        // 3. UI Navigation: Open application using seeded credentials context
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        Assert.assertTrue(inventoryPage.isPageLoaded(), "Inventory page should load");

        // 4. UI Actions: Perform purchase workflow with verified cart loading
        inventoryPage.addBackpackToCart();
        Assert.assertEquals(inventoryPage.getCartItemBadgeCount(), "1", "Cart item count badge should display '1'");

        CartPage cartPage = inventoryPage.clickShoppingCart();
        Assert.assertTrue(cartPage.isPageLoaded(), "Cart page should be loaded before proceeding to checkout");
        Assert.assertEquals(cartPage.getFirstItemName(), "Sauce Labs Backpack", "Product name in cart must match");

        cartPage.clickCheckout();

        CheckoutPage checkoutPage = new CheckoutPage(DriverManager.getDriver());
        checkoutPage.enterInformation(seededUserName, "Automation", "90210")
                    .clickContinue()
                    .clickFinish();

        Assert.assertTrue(checkoutPage.isOrderComplete(), "Order placed on UI with API-seeded user data should succeed");

        // 5. API Final Verification: Delete/Clean up API record
        Response deleteResponse = userClient.deleteUser(2);
        int deleteStatus = deleteResponse.getStatusCode();
        if (deleteStatus == 429) {
            log.warn("Teardown rate-limited: HTTP 429 returned from ReqRes");
        } else {
            Assert.assertEquals(deleteStatus, 204, "API teardown should return HTTP 204 No Content");
        }
        log.info("API Teardown Successful: Cleaned up integration test record");
    }
}
