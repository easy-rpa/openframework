package eu.easyrpa.examples.google.sheets.working_with_cell_formulas;

import eu.easyrpa.examples.google.sheets.working_with_cell_formulas.tasks.EditCellFormulas;
import eu.easyrpa.examples.google.sheets.working_with_cell_formulas.tasks.EvaluateFormulas;
import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Working with Cell Formulas")
public class WorkingWithCellFormulasModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), EvaluateFormulas.class)
                .thenCompose(execute(EditCellFormulas.class))
                .get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(WorkingWithCellFormulasModule.class);
    }
}
