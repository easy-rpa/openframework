package eu.easyrpa.examples.azure.services.onedrive_files_listing.tasks;

import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.requests.DriveItemCollectionPage;
import com.microsoft.graph.requests.GraphServiceClient;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import services.GraphServiceProvider;

import javax.inject.Inject;

@Slf4j
@ApTaskEntry(name = "Getting all files from OneDrive example task")
public class OneDriveFilesListing extends ApTask {

    @Inject
    private GraphServiceProvider graphServiceProvider;

    @Override
    public void execute() {
        GraphServiceClient<Request> graphClient = graphServiceProvider.getGraphServiceClient();

        DriveItemCollectionPage children = graphClient.me().drive().root().children()
                .buildRequest()
                .get();

        if (children != null) {
            for (DriveItem child : children.getCurrentPage()) {
                log.info(child.name);
            }
        }
    }
}
