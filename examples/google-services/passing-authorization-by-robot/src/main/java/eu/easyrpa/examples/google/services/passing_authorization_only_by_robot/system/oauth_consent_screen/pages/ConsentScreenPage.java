package eu.easyrpa.examples.google.services.passing_authorization_only_by_robot.system.oauth_consent_screen.pages;

import eu.ibagroup.easyrpa.engine.rpa.locator.By;
import eu.ibagroup.easyrpa.engine.rpa.page.WebPage;
import eu.ibagroup.easyrpa.engine.rpa.po.annotation.Wait;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ConsentScreenPage extends WebPage {

    private static final String XPATH_COMMON_PART = "//link[contains(@rel, 'stylesheet')]/following::body";

    @FindBy(xpath = XPATH_COMMON_PART + "/descendant::a/parent::div/a[contains(@href, '#')]")
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement additionalSettings; //OAuthConsentScreen production mode

    @FindBy(xpath = XPATH_COMMON_PART + "/descendant::p/a[contains(@href, '#')]")
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement openOauthScreen;  //OAuthConsentScreen production mode

    @FindBy(xpath = "//div[contains(@id,'view_container')]/descendant::span[4]")
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement submitGrantPermission; //OAuthConsentScreen test mode

    @FindBy(xpath = "//div[contains(@id, 'submit_approve_access')]/descendant::span")
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement oauthScreenTrust;

    public void grantAccessToApplication(boolean isTestMode) {
        if (isTestMode) {
            submitGrantPermission.click();
            oauthScreenTrust.click();
        } else {
            additionalSettings.click();
            openOauthScreen.click();
            oauthScreenTrust.click();
        }
        getDriver().waitFor(ExpectedConditions.presenceOfElementLocated(By
                .xpath("//body[contains(text(),'Received verification code. You may now close this window.')]")), 10, false, true);
    }
}
