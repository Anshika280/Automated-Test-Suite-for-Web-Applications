package tests.ui;

import framework.base.BaseTest;
import framework.driver.DriverManager;
import framework.models.LoginDataModel;
import framework.pages.InventoryPage;
import framework.pages.LoginPage;
import framework.utils.JsonDataReader;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DataDrivenLoginTest extends BaseTest {

    @DataProvider(name = "loginDataSupplier")
    public Object[][] getLoginData() {
        return JsonDataReader.getLoginDataArray("loginData.json");
    }

    @Test(dataProvider = "loginDataSupplier", groups = {"datadriven", "regression"})
    public void testDataDrivenLogin(LoginDataModel loginData) {
        log.info("Executing Data-Driven Test Case: [{}]", loginData.getTestCaseName());
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());

        if (loginData.isSuccess()) {
            InventoryPage inventoryPage = loginPage.login(loginData.getUsername(), loginData.getPassword());
            Assert.assertTrue(inventoryPage.isPageLoaded(),"Inventory page should be visible for valid login test case: " + loginData.getTestCaseName());
        } else {
            loginPage.enterUsername(loginData.getUsername())
                     .enterPassword(loginData.getPassword())
                     .clickLoginExpectingFailure();

            Assert.assertTrue(loginPage.isErrorMessageDisplayed(),"Error message should be displayed for test case: " + loginData.getTestCaseName());
            Assert.assertTrue(loginPage.getErrorMessageText().contains(loginData.getExpectedErrorMessage()),
                    String.format("Expected error message to contain '%s', but was: '%s'",loginData.getExpectedErrorMessage(), loginPage.getErrorMessageText()));
        }
    }
}
