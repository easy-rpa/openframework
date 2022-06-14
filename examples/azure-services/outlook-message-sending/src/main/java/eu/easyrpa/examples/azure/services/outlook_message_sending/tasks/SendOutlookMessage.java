package eu.easyrpa.examples.azure.services.outlook_message_sending.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;

//@Slf4j
//@ApTaskEntry(name = "Send Outlook message")
//public class SendOutlookMessage extends ApTask {
//
//   // private AzureAuth azureAuth;
//
////    @Override
////    public void execute() throws Exception {
////            sendMail("Bruh", "Bruh", "Kossart2001@gmail.com");
////    }
////
////    public static void sendMail(String subject, String body, String recipient) throws Exception {
////        // Ensure client isn't null
////        if (_userClient == null) {
////            throw new Exception("Graph has not been initialized for user auth");
////        }
////
////        // Create a new message
////        final Message message = new Message();
////        message.subject = subject;
////        message.body = new ItemBody();
////        message.body.content = body;
////        message.body.contentType = BodyType.TEXT;
////
////        final Recipient toRecipient = new Recipient();
////        toRecipient.emailAddress = new EmailAddress();
////        toRecipient.emailAddress.address = recipient;
////        message.toRecipients = List.of(toRecipient);
////
////        // Send the message
////        _userClient.me()
////                .sendMail(UserSendMailParameterSet.newBuilder()
////                        .withMessage(message)
////                        .build())
////                .buildRequest()
////                .post();
////    }
//}
