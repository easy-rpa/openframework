package eu.easyrpa.examples.google.services.passing_authorization_only_by_robot.system.oauth_consent_screen.pages;

import eu.ibagroup.easyrpa.engine.model.SecretCredentials;
import eu.ibagroup.easyrpa.engine.rpa.page.WebPage;
import eu.ibagroup.easyrpa.engine.rpa.po.annotation.Wait;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Slf4j
public class LoginPage extends WebPage implements AutoCloseable {

    @FindBy(xpath = "//input[contains(@id, 'identifierId')]")
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement email;

    @FindBy(xpath = "//button[contains(@type, 'button')]/child::span")
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement submitButton;

    @FindBy(xpath = "//div[contains(@id, 'view_container')]/descendant::ul/descendant::li[last()-1]")
    @Wait(waitFunc = Wait.WaitFunc.PRESENCE)
    private WebElement chooseAccount;

    public PasswordPage openPasswordPage(String userCred) {
        try {
            chooseAccount.click();
        } catch (Exception e) {
            log.info("Cannot switch account, authorization in default window");
        }
        email.click();
        email.sendKeys(userCred);
        submitButton.click();
        return createPage(PasswordPage.class);
    }

    @Override
    public void close() throws Exception {
        getDriver().close();
        log.info("Login page is closed");
    }
}
