package eu.easyrpa.examples.google.services.passing_authorization_only_by_robot.task;

import de.taimos.totp.TOTP;
import eu.easyrpa.examples.google.services.passing_authorization_only_by_robot.system.oauth_consent_screen.OAuthConsentScreenApplication;
import eu.easyrpa.examples.google.services.passing_authorization_only_by_robot.system.oauth_consent_screen.pages.LoginPage;
import eu.easyrpa.examples.google.services.passing_authorization_only_by_robot.system.oauth_consent_screen.pages.PasswordPage;
import eu.easyrpa.openframework.google.drive.GoogleDrive;
import eu.easyrpa.openframework.google.drive.model.GFileInfo;
import eu.ibagroup.easyrpa.engine.annotation.*;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.engine.model.SecretCredentials;
import eu.ibagroup.easyrpa.engine.rpa.driver.BrowserDriver;
import eu.ibagroup.easyrpa.engine.rpa.driver.DriverParams;
import eu.ibagroup.easyrpa.engine.service.VaultService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

@Slf4j
@ApTaskEntry(name = "List All Files")
public class ListAllFiles extends ApTask {

    @Driver(value = DriverParams.Type.BROWSER, param =
            {@DriverParameter(key = DriverParams.Browser.SELENIUM_NODE_CAPABILITIES,
                    initializer = BrowserDriver.DefaultChromeOptions.class)})
    private BrowserDriver browserDriver;

    @Configuration(value = "google.services.auth.creds")
    private String oauthConsentScreenCredentials;

    @Configuration(value = "google.services.secret.code")
    private String secretKey;

    @Inject
    private VaultService vaultService;

    @Inject
    private GoogleDrive drive;

    @AfterInit
    public void init() {
        drive.onAuthorization(url -> {
            try(LoginPage loginPage = new OAuthConsentScreenApplication(browserDriver).open(new String[]{url})) {
                log.info("Authorization is started");
                PasswordPage passwordPage = loginPage.openPasswordPage(vaultService.getSecret(oauthConsentScreenCredentials).getUser());
                passwordPage.openTOTPPage(vaultService.getSecret(oauthConsentScreenCredentials).getPassword())
                        .enterTOTPCode(getTOTPCode(vaultService.getSecret(secretKey).getUser().toUpperCase()));
                log.info("Authorization is finished");
            } catch (Exception e) {
                log.info("Authorization has failed '{}'", e.getMessage());
            }
        });
    }

    public void execute() {
        List<GFileInfo> files = drive.listFiles();
        log.info("Getting the list of all files");
        files.forEach(file -> log.info("Name: '{}' id: '{}'", file.getName(), file.getId()));
    }

    private String getTOTPCode(String googleAccountSecretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(googleAccountSecretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }
}