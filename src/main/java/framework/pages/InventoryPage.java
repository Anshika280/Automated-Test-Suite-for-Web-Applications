package framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

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
        return isDisplayed(pageTitle, 10) && getText(pageTitle).equalsIgnoreCase("Products");
    }

    public String getPageTitleText() {
        return getText(pageTitle);
    }

    public InventoryPage addBackpackToCart() {
        click(backpackAddToCartButton);
        return this;
    }

    public InventoryPage removeBackpackFromCart() {
        click(backpackRemoveButton);
        return this;
    }

    public boolean isCartBadgeDisplayed() {
        return isDisplayed(shoppingCartBadge, 2);
    }

    public String getCartItemBadgeCount() {
        return getText(shoppingCartBadge);
    }

    public CartPage clickShoppingCart() {
        click(shoppingCartLink);
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
