package framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class CheckoutPage extends BasePage {

    // Step One Locators
    private final By firstNameInput = By.id("first-name");
    private final By lastNameInput = By.id("last-name");
    private final By postalCodeInput = By.id("postal-code");
    private final By continueButton = By.id("continue");

    // Step Two Locators
    private final By finishButton = By.id("finish");
    private final By completeHeader = By.className("complete-header");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public CheckoutPage enterInformation(String firstName, String lastName, String postalCode) {
        String safeFirst = (firstName != null && !firstName.trim().isEmpty()) ? firstName : "DefaultFirst";
        String safeLast = (lastName != null && !lastName.trim().isEmpty()) ? lastName : "DefaultLast";
        String safeZip = (postalCode != null && !postalCode.trim().isEmpty()) ? postalCode : "12345";

        sendKeys(firstNameInput, safeFirst);
        sendKeys(lastNameInput, safeLast);
        sendKeys(postalCodeInput, safeZip);
        return this;
    }

    public CheckoutPage clickContinue() {
        log.info("Clicking continue button on checkout step one");
        click(continueButton);
        try {
            waitUtils.waitForVisibility(finishButton, 5);
        } catch (Exception e) {
            log.warn("Finish button not visible after standard click, executing JS click fallback on continue button...");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(continueButton));
            waitUtils.waitForVisibility(finishButton, 15);
        }
        return this;
    }

    public CheckoutPage clickFinish() {
        log.info("Clicking finish button on checkout step two");
        click(finishButton);
        try {
            waitUtils.waitForVisibility(completeHeader, 5);
        } catch (Exception e) {
            log.warn("Complete header not visible after standard finish click, executing JS click fallback on finish button...");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(finishButton));
            waitUtils.waitForVisibility(completeHeader, 15);
        }
        return this;
    }

    public String getCompleteHeaderText() {
        return getText(completeHeader);
    }

    public boolean isOrderComplete() {
        return isDisplayed(completeHeader, 15) && getCompleteHeaderText().equalsIgnoreCase("Thank you for your order!");
    }
}
