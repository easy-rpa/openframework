package eu.easyrpa.examples.azure.services.outlook_message_reading.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import services.OutlookEmailService;

import javax.inject.Inject;

@ApTaskEntry(name = "btuh")
public class ReadMessagesTask extends ApTask {

    @Inject
    private OutlookEmailService outlookEmailService;

    @Override
    public void execute() throws Exception {
        outlookEmailService.sendMail("Bruh","Bruh", "Bruh");
    }
}
