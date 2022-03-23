package eu.easyrpa.examples.google.services.passing_authorization_only_by_robot.task;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.google.drive.GoogleDrive;
import eu.easyrpa.openframework.google.drive.model.GFileInfo;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

@Slf4j
@ApTaskEntry(name = "List All Files")
public class ListAllFiles extends ApTask {

    @Inject
    private GoogleDrive drive;

    public void execute() {

        //Specify custom logic of passing authorization flow
        drive.onAuthorization(url->{
           //TODO implement opening of url in browser and passing of all authorization steps automatically
        });

        log.info("Getting the list of all files");
        List<GFileInfo> files = drive.listFiles();

        files.forEach(file -> log.info("Name: '{}' id: '{}'", file.getName(), file.getId()));
    }
}