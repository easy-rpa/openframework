package services;

import com.microsoft.graph.models.*;

import java.util.List;

public class MessageBuilder {
    private  Message message;

    public Message getMessage(){
        return message;
    }

    private MessageBuilder(){

    }

    public static Builder newBuilder(){
        return new MessageBuilder().new Builder();
    }
    public class Builder{

        private Builder(){
            MessageBuilder.this.message = new Message();
        }

        public Builder setMessageSubject(String subject){
            MessageBuilder.this.message.subject = subject;
            return  this;
        }

        public Builder setMessageContent(String messageContent){
            MessageBuilder.this.message.body = new ItemBody();
            MessageBuilder.this.message.body.content = messageContent;
            MessageBuilder.this.message.body.contentType = BodyType.TEXT;
            return  this;
        }

        public  Builder setRecipient(String emailRecipient){
            Recipient toRecipient = new Recipient();
            toRecipient.emailAddress = new EmailAddress();
            toRecipient.emailAddress.address = emailRecipient;
            MessageBuilder.this.message.toRecipients = List.of(toRecipient);
            return  this;
        }

        public  MessageBuilder build(){
            return  MessageBuilder.this;
        }



    }
}
