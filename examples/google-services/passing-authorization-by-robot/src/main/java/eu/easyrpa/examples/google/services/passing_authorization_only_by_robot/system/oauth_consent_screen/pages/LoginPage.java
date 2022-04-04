package eu.easyrpa.examples.google.services.passing_authorization_only_by_robot.system.oauth_consent_screen.pages;

import eu.ibagroup.easyrpa.engine.model.SecretCredentials;
import eu.ibagroup.easyrpa.engine.rpa.page.WebPage;
import eu.ibagroup.easyrpa.engine.rpa.po.annotation.Wait;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.NoSuchElementException;

@Slf4j
public class LoginPage extends WebPage implements AutoCloseable {

    @FindBy(xpath = "//input[contains(@id, 'identifierId')]")
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement email;

    @FindBy(xpath = "//button[contains(@type, 'button')]/child::span")
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement submitButton;

    @FindBy(xpath = "//input[contains(@name, 'password')]")
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement password;

    @FindBy(xpath = "//div[contains(@id, 'view_container')]/descendant::div[contains(text(), 'Use another')]")
    @Wait(waitFunc = Wait.WaitFunc.PRESENCE)
    private WebElement chooseAccount;

    @FindBy(xpath = "//div[contains(@id,'selectionc4')]")
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement notAgreeButton;

    @FindBy(xpath = "//input[contains(@id,'totpPin')]")
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement oneTimeCode;

    public ConsentScreenPage confirmLogin(SecretCredentials credentials, String code) {
        try {
            email.click();
        } catch (NoSuchElementException e) {
            chooseAccount.click();
            email.click();
        }
        email.sendKeys(credentials.getUser());
        submitButton.click();
        password.click();
        password.sendKeys(credentials.getPassword());
        submitButton.click();
        notAgreeButton.click();
        oneTimeCode.click();
        oneTimeCode.sendKeys(code);
        submitButton.click();
        return createPage(ConsentScreenPage.class);
    }

    @Override
    public void close() {
        getDriver().close();
    }
}
