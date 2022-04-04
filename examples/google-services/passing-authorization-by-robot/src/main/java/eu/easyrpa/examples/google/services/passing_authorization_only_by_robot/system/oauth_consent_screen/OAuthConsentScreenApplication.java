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
        try {
            if (args[0] == null) {
                throw new Exception("Invalid URL");
            }
            String googleAuthUrl = args[0];
            getDriver().get(googleAuthUrl);
        } catch (Exception e) {
            log.info("Cannot open browser window : '{}'", e.getMessage());
        }
        return createPage(LoginPage.class);
    }
}
