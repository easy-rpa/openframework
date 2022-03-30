package eu.easyrpa.examples.google.services.passing_authorization_only_by_robot.system.iba.OAuth_consert_screen.pages;

import eu.ibagroup.easyrpa.engine.model.SecretCredentials;
import eu.ibagroup.easyrpa.engine.rpa.page.WebPage;
import eu.ibagroup.easyrpa.engine.rpa.po.annotation.Wait;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Slf4j
public class LoginPage extends WebPage {

    public static final String XPATH_LOGIN = "/html/body/div[1]/div[1]/div[2]/div/div[2]/div/div/div[2]/div/div[1]/div/form/span/section/div/div/div[1]/div/div[1]/div/div[1]/input";
    public static final String XPATH_SUBMIT_BUTTON = "/html/body/div[1]/div[1]/div[2]/div/div[2]/div/div/div[2]/div/div[2]/div/div[1]/div/div/button/span";
    public static final String XPATH_PASSWORD = "/html/body/div[1]/div[1]/div[2]/div/div[2]/div/div/div[2]/div/div[1]/div/form/span/section/div/div/div[1]/div[1]/div/div/div/div/div[1]/div/div[1]/input";
    public static final String XPATH_DONT_AGREE = "/html/body/div[1]/div[1]/div[2]/div/div[2]/div/div/div[2]/div/div[1]/div/form/span/section/div/div/div[3]/div[1]/div/div/div[2]/div/div";
    public static final String XPATH_AUTH_FIELD = "/html/body/div[1]/div[1]/div[2]/div/div[2]/div/div/div[2]/div/div[1]/div/form/span/section/div/div/div[2]/div/div[1]/div/div[1]/input";

    @FindBy(xpath = XPATH_LOGIN)
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement email;

    @FindBy(xpath = XPATH_SUBMIT_BUTTON)
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement submitButton;

    @FindBy(xpath = XPATH_PASSWORD)
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement password;

    @FindBy(xpath = XPATH_DONT_AGREE)
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement notAgreeButton;

    @FindBy(xpath = XPATH_AUTH_FIELD)
    @Wait(waitFunc = Wait.WaitFunc.CLICKABLE)
    private WebElement oneTimeCode;

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
    }
}
