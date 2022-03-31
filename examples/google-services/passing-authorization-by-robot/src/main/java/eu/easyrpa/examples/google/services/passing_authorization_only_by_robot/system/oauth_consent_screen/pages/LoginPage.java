package eu.easyrpa.examples.google.services.passing_authorization_only_by_robot.system.oauth_consent_screen.pages;

import eu.ibagroup.easyrpa.engine.model.SecretCredentials;
import eu.ibagroup.easyrpa.engine.rpa.page.WebPage;
import eu.ibagroup.easyrpa.engine.rpa.po.annotation.Wait;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Slf4j
public class LoginPage extends WebPage implements AutoCloseable {

    @FindBy(id = "identifierId")
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement email;

    @FindBy(className = "VfPpkd-vQzf8d")
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement submitButton;

    @FindBy(name = "password")
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement password;

    @FindBy(xpath = "//div[contains(@id,'selectionc4')]")
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement notAgreeButton;

    @FindBy(xpath = "//*[@id=\"totpPin\"]")
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement oneTimeCode;

    @FindBy(xpath = "//body[contains(text(),'Received verification code. You may now close this window.')]")
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement oathConsentScreenResult;

    public void login(SecretCredentials googleAccountCredentials, String code) {
        email.click();
        email.sendKeys(googleAccountCredentials.getUser());
        submitButton.click();
        password.click();
        password.sendKeys(googleAccountCredentials.getPassword());
        submitButton.click();
        notAgreeButton.click();
        oneTimeCode.click();
        oneTimeCode.sendKeys(code);
        submitButton.click();
        oathConsentScreenResult.click();
    }

    @Override
    public void close() throws Exception {
        this.getDriver().close();
        log.info("Driver has been closed");
    }
}
