# Email

## Table of Contents
* [Description](#description)
* [Supported protocols](#supported-protocols)
* [Usage](#usage)
* [Send Email with Attachment](#send-email-with-attachment)
* [Get inbox messages](#get-inbox-messages)
* [Example](#example)

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

## Usage

To start use the library first you need to add corresponding Maven dependency to your project:

![mavenVersion](https://img.shields.io/maven-central/v/eu.ibagroup/easy-rpa-openframework-email)

```java
<dependency>
    <groupId>eu.ibagroup</groupId>
    <artifactId>easy-rpa-openframework-email</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```


## Send Email with Attachment

In this example we will send simple email message with attachment.
First step - place configuration parameters:

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `outbound.email.server` | Host name and port of email server for outbound emails |
| `outbound.email.protocol` | Protocol which is used by email server for outbound emails |
| `email.recipients` | Email address where email message will be sent |

**vault.properties**

| Alias     | Value         |
| ------------- |---------------|
| `email.user` | Json with credentials in encoded with Base64. Example of json:<br>`{ "user": "sender@gmail.com", "password": "passphrase" }` |

Note: All necessary configuration files can be found in `src/main/resources` directory.

After that we can use these configuration values inside the main task:
```java
    @Configuration(value = "outbound.email.server")
    private String outboundEmailServer;

    @Configuration(value = "outbound.email.protocol")
    private String outboundEmailProtocol;

    @Configuration(value = "email.recipients")
    private String emailRecipients;

    @Configuration(value = "email.user")
    private SecretCredentials emailUserCredentials;
```

Also we inject object of EmailSender class:

```java
    @Inject
    private EmailSender emailSender;
```

Finally we can send an email with a file attached to it:

```java
    @Override
    public void execute() throws IOException {
        log.info("Send email messages to '{}' using service '{}', protocol '{}' and mailbox '{}'.",
                emailRecipients, outboundEmailServer, outboundEmailProtocol, emailUserCredentials.getUser());

        log.info("Read '{}' file.", PATH_TO_FILE);
        File testFile = new File(this.getClass().getResource("/Test.xlsx").toURI());

        private static final String ATTACHMENT_SUBJECT = "Test email with attachment";
        private static final String ATTACHMENT_BODY = "This message was sent by EasyRPA Bot and has attached file.";
        
        log.info("Send message with attached file.");
        new EmailMessage(emailSender).subject(ATTACHMENT_SUBJECT).html(ATTACHMENT_BODY).attach(testFile).send();
        
        log.info("Messages have been sent successfully");
    }
```
## Get inbox messages

In this example we will get a list of all inbox emails.

First we will configure mailbox credentials and inject object of 'EmailClient' class:

```java
    @Configuration(value = "mailbox")
    private SecretCredentials mailboxCredentials;

    @Inject
    private EmailClient emailClient;
```

After that we can easily get all the messages using function 'fetchMessages':

```java
    @Override
    public void execute() {

        log.info("Fetch messages using email client.");
        List<EmailMessage> messages = emailClient.fetchMessages();

        log.info("List fetched messages:");
        messages.forEach(msg -> {
            log.info("'{}' from '{}'", msg.getSubject(), msg.getSender().getPersonal());
        });
    }
}
```

## Example

For more code examples please refer to corresponding [article](https://github.com/easyrpa/openframework/tree/main/examples#email). 