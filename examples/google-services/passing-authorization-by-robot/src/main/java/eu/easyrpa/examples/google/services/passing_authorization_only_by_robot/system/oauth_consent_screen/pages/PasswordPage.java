package eu.easyrpa.examples.google.services.passing_authorization_only_by_robot.system.oauth_consent_screen.pages;

import eu.ibagroup.easyrpa.engine.rpa.page.WebPage;
import eu.ibagroup.easyrpa.engine.rpa.po.annotation.Wait;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Slf4j
public class PasswordPage extends WebPage {

    @FindBy(xpath = "//input[contains(@name, 'password')]")
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement password;

    @FindBy(xpath = "//button[contains(@type, 'button')]/child::span")
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement submitButton;

    public TOTPPage openTOTPPage(String passwordCred) {
        password.click();
        password.sendKeys(passwordCred);
        submitButton.click();
        return createPage(TOTPPage.class);
    }
}
