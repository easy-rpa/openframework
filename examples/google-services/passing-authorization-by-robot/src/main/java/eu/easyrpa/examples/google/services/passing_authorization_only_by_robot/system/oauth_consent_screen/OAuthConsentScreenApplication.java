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
        if (args == null || args.length == 0 || args[0] == null) {
            throw new IllegalArgumentException("Consent screen URL is not defined or missing");
        }
        getDriver().get(args[0]);
        return createPage(LoginPage.class);
    }
}
