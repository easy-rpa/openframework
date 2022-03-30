package eu.easyrpa.examples.google.services.passing_authorization_only_by_robot.system.iba.OAuth_consert_screen;

import eu.easyrpa.examples.google.services.passing_authorization_only_by_robot.system.iba.OAuth_consert_screen.pages.LoginPage;
import eu.ibagroup.easyrpa.engine.rpa.Application;
import eu.ibagroup.easyrpa.engine.rpa.driver.BrowserDriver;
import eu.ibagroup.easyrpa.engine.rpa.element.UiElement;

public class OAuthConsentScreenApplication extends Application<BrowserDriver, UiElement> {

    public OAuthConsentScreenApplication(BrowserDriver driver) {
        super(driver);
    }

    @Override
    public LoginPage open(String... args) {
        String googleAuthUrl = args[0];
        getDriver().get(googleAuthUrl);
        LoginPage loginPage = createPage(LoginPage.class);
        return loginPage;
    }
}
