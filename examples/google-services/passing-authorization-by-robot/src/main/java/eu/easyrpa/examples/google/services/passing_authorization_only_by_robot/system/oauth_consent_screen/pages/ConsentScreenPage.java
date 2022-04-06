package eu.easyrpa.examples.google.services.passing_authorization_only_by_robot.system.oauth_consent_screen.pages;

import eu.ibagroup.easyrpa.engine.rpa.page.WebPage;
import eu.ibagroup.easyrpa.engine.rpa.po.annotation.Wait;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class ConsentScreenPage extends WebPage {

    private final By By_OauthScreenResult = By.xpath("//body[contains(text(),'Received verification code. You may now close this window.')]");

    @FindBy(xpath = "//link[contains(@rel, 'stylesheet')]/following::body/descendant::a/parent::div/a[contains(@href, '#')]")
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement additionalSettings; //OAuthConsentScreen test mode

    @FindBy(xpath = "//link[contains(@rel, 'stylesheet')]/following::body/descendant::p/a[contains(@href, '#')]")
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement openOauthScreen;  //OAuthConsentScreen test mode

    @FindBy(xpath = "//div[contains(@id,'view_container')]/descendant::span[4]")
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement submitGrantPermission; //OAuthConsentScreen production mode

    @FindBy(xpath = "//div[contains(@id, 'submit_approve_access')]/descendant::span")
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement oauthScreenTrust;

    public void grantAccess() {
        try {
            submitGrantPermission.click();
        } catch (TimeoutException e) {
            additionalSettings.click();
            openOauthScreen.click();
        }
        oauthScreenTrust.click();
        getDriver().waitFor(ExpectedConditions.presenceOfElementLocated(By_OauthScreenResult), 10, false, true);
    }
}
