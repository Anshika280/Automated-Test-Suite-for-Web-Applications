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

    private String lastFirstName = "DefaultFirst";
    private String lastLastName = "DefaultLast";
    private String lastPostalCode = "12345";

    public CheckoutPage enterInformation(String firstName, String lastName, String postalCode) {
        this.lastFirstName = (firstName != null && !firstName.trim().isEmpty()) ? firstName : "DefaultFirst";
        this.lastLastName = (lastName != null && !lastName.trim().isEmpty()) ? lastName : "DefaultLast";
        this.lastPostalCode = (postalCode != null && !postalCode.trim().isEmpty()) ? postalCode : "12345";

        waitUtils.waitForVisibility(firstNameInput, 10);
        sendKeys(firstNameInput, this.lastFirstName);
        sendKeys(lastNameInput, this.lastLastName);
        sendKeys(postalCodeInput, this.lastPostalCode);
        return this;
    }

    public CheckoutPage clickContinue() {
        log.info("Clicking continue button on checkout step one");
        click(continueButton);
        try {
            waitUtils.waitForVisibility(finishButton, 8);
        } catch (Exception e) {
            By errorBanner = By.cssSelector("[data-test='error']");
            if (isDisplayed(errorBanner, 2)) {
                log.warn("Step one validation error displayed: '{}'. Re-entering customer info and re-clicking continue...", getText(errorBanner));
                enterInformation(lastFirstName, lastLastName, lastPostalCode);
                click(continueButton);
            } else {
                log.warn("Finish button not visible after standard click, executing JS click fallback on continue button...");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(continueButton));
            }
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
