package eu.easyrpa.examples.excel.cell_style_changing;

import eu.easyrpa.examples.excel.cell_style_changing.tasks.SetStyleForCells;
import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Cell Styles Changing")
public class CellStylesChangingModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), SetStyleForCells.class).get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(CellStylesChangingModule.class);
    }
}
