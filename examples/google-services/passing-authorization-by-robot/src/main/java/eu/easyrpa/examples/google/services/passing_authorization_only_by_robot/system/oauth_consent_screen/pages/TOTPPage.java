package eu.easyrpa.examples.google.services.passing_authorization_only_by_robot.system.oauth_consent_screen.pages;

import eu.ibagroup.easyrpa.engine.rpa.page.WebPage;
import eu.ibagroup.easyrpa.engine.rpa.po.annotation.Wait;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@Slf4j
public class TOTPPage extends WebPage {

    @FindBy(xpath = "//div[contains(@id,'selectionc4')]")
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement notAgreeButton;

    @FindBy(xpath = "//input[contains(@id,'totpPin')]")
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement oneTimeCode;

    @FindBy(xpath = "//button[contains(@type, 'button')]/child::span")
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement submitButton;

    public void enterTOTPCode(String code) {
        notAgreeButton.click();
        oneTimeCode.click();
        oneTimeCode.sendKeys(code);
        submitButton.click();
        WebDriverWait wait = new WebDriverWait(getDriver(), 10);
        wait.until(ExpectedConditions.presenceOfElementLocated(By
                .xpath("//body[contains(text(),'Received verification code. You may now close this window.')]")));
    }
}
