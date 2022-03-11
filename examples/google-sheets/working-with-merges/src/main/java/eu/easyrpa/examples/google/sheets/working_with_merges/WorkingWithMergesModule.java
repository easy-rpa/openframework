package eu.easyrpa.examples.google.sheets.working_with_merges;

import eu.easyrpa.examples.google.sheets.working_with_merges.tasks.MergeUnmergeCells;
import eu.easyrpa.examples.google.sheets.working_with_merges.tasks.ReadEditMergedCellsValues;
import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Working with Merges")
public class WorkingWithMergesModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), MergeUnmergeCells.class)
                .thenCompose(execute(ReadEditMergedCellsValues.class))
                .get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(WorkingWithMergesModule.class);
    }
}
