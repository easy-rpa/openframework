# Email

## Table of Contents
* [Description](#description)
* [Supported protocols](#supported-protocols)
* [Quick start](#quick-start)
* [Configuration](#configuration)
* [Running](#running)
* [How To](#how-to)
* [Examples](#examples)

## Description

Component which provides functionality related to Emails.

## Supported protocols

* SMTP
* SMTP over SSL
* SMTP over TCL
* IMAP/IMAPS
* POP3
* EWS
* MAPI

## Quick Start
To start use Email library first you need to add corresponding Maven dependency to your project:

![mavenVersion](https://img.shields.io/maven-central/v/eu.ibagroup/easy-rpa-openframework-email)

```java
<dependency>
    <groupId>eu.ibagroup</groupId>
    <artifactId>easy-rpa-openframework-email</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

After that you need build the project via Maven command:

```java
mvn clean install
```
After execution of this command you should see such message:

![Screenshot-1.png](https://i.postimg.cc/s2Dmc3w1/Screenshot-1.png)

## Configuration

All necessary configuration files can be found in `src/main/resources` directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `outbound.email.server` | Host name and port of email server for outbound emails |
| `outbound.email.protocol` | Protocol which is used by email server for outbound emails |
| `outbound.email.secret` | Vault alias that contains credentials for authentication on the email server for outbound emails |
| `email.sender.name` | Name of email sender that will be display as "from" for email recipients |
| `email.recipients` | Email address where email message will be sent |

**vault.properties**

| Alias     | Value         |
| ------------- |---------------|
| `email.user` | Json with credentials in encoded with Base64. Example of json:<br>`{ "user": "sender@gmail.com", "password": "passphrase" }` |

## Running

Run `main()` method of `LocalRunner` class.

## How To
In this section you can find examples of most popular actions you can perform using Email library from the Open Framework.

* **Send Email with Attachment**
```java
package eu.ibagroup.easyrpa.examples.email.message_sending_with_attachments.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.engine.model.SecretCredentials;
import eu.ibagroup.easyrpa.openframework.email.EmailMessage;
import eu.ibagroup.easyrpa.openframework.email.EmailSender;
import eu.ibagroup.easyrpa.openframework.email.message.EmailAttachment;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

@ApTaskEntry(name = "Send Message with Attachment")
@Slf4j
public class SendMessageWithAttachment extends ApTask {

    private static final String PATH_TO_FILE = "/Test.xlsx";
    private static final String ATTACHMENT_SUBJECT = "Test email with attachment";
    private static final String ATTACHMENT_BODY = "This message was sent by EasyRPA Bot and has attached file.";

    private static final String PATH_TO_IMAGE = "/Image.png";
    private static final String INLINE_ATTACHMENT_SUBJECT = "Test email with inline attachment";
    private static final String INLINE_ATTACHMENT_BODY_TPL = "This message was sent by EasyRPA Bot and " +
            "includes an image: <br> %s <br> Some text in the end.";

    @Configuration(value = "outbound.email.server")
    private String outboundEmailServer;

    @Configuration(value = "outbound.email.protocol")
    private String outboundEmailProtocol;

    @Configuration(value = "email.recipients")
    private String emailRecipients;

    @Configuration(value = "email.user")
    private SecretCredentials emailUserCredentials;

    @Inject
    private EmailSender emailSender;

    @Override
    public void execute() throws IOException {
        log.info("Send email messages to '{}' using service '{}', protocol '{}' and mailbox '{}'.",
                emailRecipients, outboundEmailServer, outboundEmailProtocol, emailUserCredentials.getUser());

        log.info("Read '{}' file.", PATH_TO_FILE);
        File testFile = readResourceFile(PATH_TO_FILE);

        log.info("Send message with attached file.");
        new EmailMessage(emailSender).subject(ATTACHMENT_SUBJECT).html(ATTACHMENT_BODY).attach(testFile).send();

        log.info("Read '{}' image.", PATH_TO_IMAGE);
        File imageFile = readResourceFile(PATH_TO_IMAGE);

        log.info("Send message with attached image in the body.");
        String body = String.format(INLINE_ATTACHMENT_BODY_TPL, EmailAttachment.getImagePlaceholder(imageFile.getName(), 541, 391));
        new EmailMessage(emailSender).subject(INLINE_ATTACHMENT_SUBJECT).html(body).attach(imageFile).send();

        log.info("Messages have been sent successfully");
    }

    public File readResourceFile(String path) {
        try {
            return new File(this.getClass().getResource(path).toURI());
        } catch (Exception e) {
            throw new RuntimeException(String.format("Reading of file '%s' has failed.", path), e);
        }
    }
}

```
* **Get inbox messages**
```java
package eu.ibagroup.easyrpa.examples.email.inbox_messages_listing.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.engine.model.SecretCredentials;
import eu.ibagroup.easyrpa.openframework.email.EmailClient;
import eu.ibagroup.easyrpa.openframework.email.EmailMessage;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

@ApTaskEntry(name = "Get Inbox Messages")
@Slf4j
public class GetInboxMessages extends ApTask {

    @Configuration(value = "mailbox")
    private SecretCredentials mailboxCredentials;

    @Inject
    private EmailClient emailClient;

    @Override
    public void execute() {

        log.info("Getting all messages from folder '{}' of '{}' mailbox.", emailClient.getDefaultFolder(), mailboxCredentials.getUser());

        log.info("There are {} messages in folder '{}'.", emailClient.getMessageCount(), emailClient.getDefaultFolder());

        log.info("Fetch messages using email client.");
        List<EmailMessage> messages = emailClient.fetchMessages();

        log.info("List fetched messages:");
        messages.forEach(msg -> {
            log.info("'{}' from '{}'", msg.getSubject(), msg.getSender().getPersonal());
        });
    }
}
```

* **Read attachment from email**
```java
package eu.ibagroup.easyrpa.examples.email.attachments_reading.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.engine.model.SecretCredentials;
import eu.ibagroup.easyrpa.openframework.email.EmailClient;
import eu.ibagroup.easyrpa.openframework.email.EmailMessage;
import eu.ibagroup.easyrpa.openframework.email.exception.BreakEmailCheckException;
import eu.ibagroup.easyrpa.openframework.email.message.EmailAttachment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.List;

@ApTaskEntry(name = "Read Messages with Attachments")
@Slf4j
public class ReadMessagesWithAttachments extends ApTask {

    @Configuration(value = "mailbox")
    private SecretCredentials mailboxCredentials;

    @Inject
    private EmailClient emailClient;

    @Override
    public void execute() {

        log.info("Lookup messages with attachments in folder '{}' of '{}' mailbox.", emailClient.getDefaultFolder(), mailboxCredentials.getUser());

        log.info("There are {} messages in folder '{}'.", emailClient.getMessageCount(), emailClient.getDefaultFolder());

        log.info("Fetch first message that contain attachments.");
        List<EmailMessage> messages = emailClient.fetchMessages(msg -> {
            log.info("Check message '{}'", msg.getSubject());
            if (msg.hasAttachments()) {
                //By throwing this exception it stops further checking of emails and return this message as single result
                throw new BreakEmailCheckException(true);
            }
            return false;
        });

        if (messages.size() > 0) {
            log.info("Message with attachments found.");
            EmailMessage msg = messages.get(0);
            EmailAttachment attachment = msg.getAttachments().get(0);

            try {
                log.info("Read content of the attached file.");
                List<String> content = IOUtils.readLines(attachment.getInputStream(), StandardCharsets.UTF_8);

                content.forEach(log::info);

            } catch (Exception e) {
                throw new RuntimeException("Reading of attached file content has failed.", e);
            }
        } else {
            log.info("No messages with attachments found in folder '{}' of '{}' mailbox.", emailClient.getDefaultFolder(), mailboxCredentials.getUser());
        }
    }
}

```

## Examples

For more code examples please refer to corresponding [article](https://github.com/dzyap/openframework/tree/main/examples#email). 