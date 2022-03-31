package eu.easyrpa.examples.google.services.passing_authorization_only_by_robot.system.oauth_consent_screen;

import eu.easyrpa.examples.google.services.passing_authorization_only_by_robot.system.oauth_consent_screen.pages.LoginPage;
import eu.ibagroup.easyrpa.engine.rpa.Application;
import eu.ibagroup.easyrpa.engine.rpa.driver.BrowserDriver;
import eu.ibagroup.easyrpa.engine.rpa.element.UiElement;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OAuthConsentScreenApplication extends Application<BrowserDriver, UiElement> {

    public OAuthConsentScreenApplication(BrowserDriver driver) {
        super(driver);
    }

    @Override
    public LoginPage open(String... args) {
        String googleAuthUrl = args[0];
        try {
            getDriver().get(googleAuthUrl);
            log.info("Open window with url : '{}'", googleAuthUrl);
        } catch (Exception e) {
            log.info("Cannot open browser window with this url : '{}' , cause : '{}'", googleAuthUrl, e.getMessage());
        }
        LoginPage loginPage = createPage(LoginPage.class);
        return loginPage;
    }
}
