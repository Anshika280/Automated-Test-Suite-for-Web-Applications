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
        try {
            boolean urlLoaded = waitUtils.waitForUrlContains("cart.html", 10);
            boolean titleLoaded = waitUtils.waitForTextToBePresentInElement(pageTitle, "Your Cart", 10);
            boolean checkoutVisible = isDisplayed(checkoutButton, 10);
            return urlLoaded && titleLoaded && checkoutVisible;
        } catch (Exception e) {
            log.warn("CartPage failed to load within timeout: {}", e.getMessage());
            return false;
        }
    }

    public String getFirstItemName() {
        waitUtils.waitForVisibility(cartItemName, 10);
        return getText(cartItemName);
    }

    public void clickCheckout() {
        log.info("Clicking checkout button on Cart page");
        waitUtils.waitForVisibility(checkoutButton, 10);
        WebElement btn = waitUtils.waitForClickability(checkoutButton, 10);
        try {
            btn.click();
        } catch (Exception e) {
            log.warn("Standard checkout click failed, using JavaScript click fallback...");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
        waitUtils.waitForUrlContains("checkout-step-one.html", 10);
    }
}
