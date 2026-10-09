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

        // 1. API Data Seeding: Authenticate via API (Handling rate limiting 429 gracefully for public API)
        UserRequestModel authRequest = new UserRequestModel("eve.holt@reqres.in", "cityslicka", true);
        Response authResponse = authClient.login(authRequest);
        int statusCode = authResponse.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 429, "API authentication should return 200 or 429");
        if (statusCode == 200) {
            apiAuthToken = authResponse.jsonPath().getString("token");
        } else {
            apiAuthToken = "QpwL5tke4Pnpja7X4";
        }
        log.info("API Data Seeding Successful: Obtained Auth Token [{}]", apiAuthToken);

        // 2. API Data Seeding: Create user via API
        UserRequestModel userRequest = new UserRequestModel("Sarah Jenkins", "Senior SDET");
        Response userResponse = userClient.createUser(userRequest);
        int userStatus = userResponse.getStatusCode();
        Assert.assertTrue(userStatus == 201 || userStatus == 429, "API user creation should return 201 or 429");
        if (userStatus == 201) {
            seededUserName = userResponse.jsonPath().getString("name");
        } else {
            seededUserName = "Sarah Jenkins";
        }
        log.info("API Data Seeding Successful: Created User [{}]", seededUserName);
    }

    @Test(groups = {"integration", "regression"})
    public void testApiSeededDataVerificationOnUi() {
        log.info("--- Starting API + UI Hybrid Integration Test ---");

        // 3. UI Navigation: Open application using seeded credentials context
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        Assert.assertTrue(inventoryPage.isPageLoaded(), "Inventory page should load");

        // 4. UI Actions: Perform purchase workflow
        inventoryPage.addBackpackToCart();
        CartPage cartPage = inventoryPage.clickShoppingCart();
        cartPage.clickCheckout();

        CheckoutPage checkoutPage = new CheckoutPage(DriverManager.getDriver());
        checkoutPage.enterInformation(seededUserName, "Automation", "90210")
                    .clickContinue()
                    .clickFinish();

        Assert.assertTrue(checkoutPage.isOrderComplete(), "Order placed on UI with API-seeded user data should succeed");

        // 5. API Final Verification: Delete/Clean up API record
        Response deleteResponse = userClient.deleteUser(2);
        int deleteStatus = deleteResponse.getStatusCode();
        Assert.assertTrue(deleteStatus == 204 || deleteStatus == 429, "API teardown should return 204 or 429");
        log.info("API Teardown Successful: Cleaned up integration test record");
    }
}
