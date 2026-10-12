package framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.List;

public class InventoryPage extends BasePage {

    // Encapsulated Page Locators
    private final By pageTitle = By.className("title");
    private final By shoppingCartBadge = By.className("shopping_cart_badge");
    private final By backpackAddToCartButton = By.id("add-to-cart-sauce-labs-backpack");
    private final By backpackRemoveButton = By.id("remove-sauce-labs-backpack");
    private final By shoppingCartLink = By.className("shopping_cart_link");
    private final By menuButton = By.id("react-burger-menu-btn");
    private final By logoutSidebarLink = By.id("logout_sidebar_link");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public boolean isPageLoaded() {
        try {
            boolean titleVisible = isDisplayed(pageTitle, 10);
            boolean titleMatches = titleVisible && getText(pageTitle).equalsIgnoreCase("Products");
            return titleMatches;
        } catch (Exception e) {
            log.warn("InventoryPage failed to load: {}", e.getMessage());
            return false;
        }
    }

    public String getPageTitleText() {
        return getText(pageTitle);
    }

    public InventoryPage addBackpackToCart() {
        log.info("Adding Sauce Labs Backpack to shopping cart");
        click(backpackAddToCartButton);
        try {
            waitUtils.waitForVisibility(backpackRemoveButton, 3);
        } catch (Exception e) {
            log.warn("Remove button not visible after clicking Add to Cart, executing JavaScript click fallback...");
            WebElement btn = driver.findElement(backpackAddToCartButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            waitUtils.waitForVisibility(backpackRemoveButton, 5);
        }
        waitUtils.waitForVisibility(shoppingCartBadge, 5);
        return this;
    }

    public InventoryPage removeBackpackFromCart() {
        log.info("Removing Sauce Labs Backpack from shopping cart");
        click(backpackRemoveButton);
        try {
            waitUtils.waitForVisibility(backpackAddToCartButton, 3);
        } catch (Exception e) {
            log.warn("Add to cart button not visible after clicking Remove, executing JavaScript click fallback...");
            WebElement btn = driver.findElement(backpackRemoveButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            waitUtils.waitForVisibility(backpackAddToCartButton, 5);
        }
        waitForCartBadgeToDisappear(5);
        return this;
    }

    public boolean isCartBadgeDisplayed() {
        try {
            List<WebElement> badges = driver.findElements(shoppingCartBadge);
            if (badges.isEmpty()) {
                return false;
            }
            WebElement badge = badges.get(0);
            if (!badge.isDisplayed()) {
                return false;
            }
            String badgeText = badge.getText().trim();
            return !badgeText.isEmpty() && !badgeText.equals("0");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean waitForCartBadgeToDisappear(int timeoutSeconds) {
        try {
            return waitUtils.waitUntilCondition(d -> !isCartBadgeDisplayed(), timeoutSeconds);
        } catch (Exception e) {
            return false;
        }
    }

    public String getCartItemBadgeCount() {
        waitUtils.waitForVisibility(shoppingCartBadge, 10);
        return getText(shoppingCartBadge);
    }

    public CartPage clickShoppingCart() {
        log.info("Navigating to Shopping Cart from Inventory page");
        click(shoppingCartLink);
        try {
            waitUtils.waitForUrlContains("cart.html", 3);
        } catch (Exception e) {
            log.warn("URL did not transition to cart.html, executing JavaScript click fallback on cart link...");
            WebElement link = driver.findElement(shoppingCartLink);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
            waitUtils.waitForUrlContains("cart.html", 10);
        }
        CartPage cartPage = new CartPage(driver);
        cartPage.isPageLoaded();
        return cartPage;
    }

    public LoginPage logout() {
        click(menuButton);
        click(logoutSidebarLink);
        return new LoginPage(driver);
    }
}
