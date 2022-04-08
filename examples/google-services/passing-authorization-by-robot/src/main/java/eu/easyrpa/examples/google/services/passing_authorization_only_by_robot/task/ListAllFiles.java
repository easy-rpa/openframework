package eu.easyrpa.examples.google.services.passing_authorization_only_by_robot.task;

import de.taimos.totp.TOTP;
import eu.easyrpa.examples.google.services.passing_authorization_only_by_robot.system.oauth_consent_screen.OAuthConsentScreenApplication;
import eu.easyrpa.examples.google.services.passing_authorization_only_by_robot.system.oauth_consent_screen.pages.LoginPage;
import eu.easyrpa.openframework.google.drive.GoogleDrive;
import eu.easyrpa.openframework.google.drive.model.GFileInfo;
import eu.ibagroup.easyrpa.engine.annotation.AfterInit;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.engine.model.SecretCredentials;
import eu.ibagroup.easyrpa.engine.rpa.driver.BrowserDriver;
import eu.ibagroup.easyrpa.engine.rpa.driver.DriverParams;
import eu.ibagroup.easyrpa.engine.rpa.driver.web.BrowserDriverImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@ApTaskEntry(name = "List All Files")
public class ListAllFiles extends ApTask {

    @Configuration(value = "google.services.auth.credentials")
    private String googleAccountCredentials;

    @Configuration(value = "google.services.auth.2fa.secret.key")
    private String googleAccountSecretKey;

    @Inject
    private GoogleDrive drive;

    @AfterInit
    public void init() {
        drive.onAuthorization(url -> {
            log.info("Authorization started");
            Map<String, Object> params = Collections.singletonMap(DriverParams.Browser.SELENIUM_NODE_CAPABILITIES,
                    new BrowserDriver.DefaultChromeOptions().get());
            BrowserDriver browserDriver = new BrowserDriverImpl(params, getConfigurationService());

            try (LoginPage loginPage = new OAuthConsentScreenApplication(browserDriver).open(url)) {
                SecretCredentials credentials = getVaultService().getSecret(googleAccountCredentials);
                String oneTimeCode = getOneTimeCode(getVaultService().getSecret(googleAccountSecretKey)
                        .getPassword().toUpperCase());

                loginPage.confirmLogin(credentials, oneTimeCode).grantAccess();

                log.info("Access granted");
            }
        });
    }

    public void execute() {
        log.info("Getting the list of all files");
        List<GFileInfo> files = drive.listFiles();
        files.forEach(file -> log.info("Name: '{}' id: '{}'", file.getName(), file.getId()));
    }

    private String getOneTimeCode(String googleAccountSecretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(googleAccountSecretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }
}