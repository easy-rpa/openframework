package firstexample.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;
import services.OutlookEmailService;

import javax.inject.Inject;

@Slf4j
@ApTaskEntry(name = "Send Outlook message")
public class SendOutlookMessage extends ApTask {

    @Inject
    private OutlookEmailService outlookEmailService;

    @Override
    public void execute() throws Exception {
        outlookEmailService.sendMail("BRuh", "Bruh", "kossart2001@gmail.com");
    }


}
