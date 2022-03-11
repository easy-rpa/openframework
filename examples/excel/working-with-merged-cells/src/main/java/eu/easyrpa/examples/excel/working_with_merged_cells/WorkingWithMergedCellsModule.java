package eu.easyrpa.examples.excel.working_with_merged_cells;

import eu.easyrpa.examples.excel.working_with_merged_cells.tasks.MergeUnmergeCells;
import eu.easyrpa.examples.excel.working_with_merged_cells.tasks.ReadEditMergedCellsValues;
import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Working with Merged Cells")
public class WorkingWithMergedCellsModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), MergeUnmergeCells.class)
                .thenCompose(execute(ReadEditMergedCellsValues.class))
                .get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(WorkingWithMergedCellsModule.class);
    }
}
