package eu.ibagroup.easyrpa.examples.database.tables_manipulating;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import eu.ibagroup.easyrpa.examples.database.tables_manipulating.tasks.DropEntityTable;
import eu.ibagroup.easyrpa.examples.database.tables_manipulating.tasks.CreateEntityTables;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Tables Manipulating")
public class TablesManipulatingModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), CreateEntityTables.class)
                .thenCompose(execute(DropEntityTable.class))
                .get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(TablesManipulatingModule.class);
    }
}
