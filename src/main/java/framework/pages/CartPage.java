package framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CartPage extends BasePage {

    private final By pageTitle = By.className("title");
    private final By cartItemName = By.className("inventory_item_name");
    private final By checkoutButton = By.id("checkout");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public boolean isPageLoaded() {
        return isDisplayed(pageTitle, 10) && getText(pageTitle).equalsIgnoreCase("Your Cart");
    }

    public String getFirstItemName() {
        return getText(cartItemName);
    }

    public void clickCheckout() {
        log.info("Clicking checkout button on Cart page");
        WebElement btn = waitUtils.waitForClickability(checkoutButton, 10);
        try {
            btn.click();
        } catch (Exception e) {
            log.warn("Standard checkout click failed, using JavaScript click fallback...");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }
}
