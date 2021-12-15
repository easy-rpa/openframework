package eu.ibagroup.easyrpa.examples.excel.working_with_formulas;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import eu.ibagroup.easyrpa.examples.excel.working_with_formulas.tasks.EditCellFormulas;
import eu.ibagroup.easyrpa.examples.excel.working_with_formulas.tasks.EvaluateFormulas;
import eu.ibagroup.easyrpa.examples.excel.working_with_formulas.tasks.EvaluateFormulasWithExternalLinks;
import eu.ibagroup.easyrpa.examples.excel.working_with_formulas.tasks.Preparation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Working with Formulas")
public class WorkingWithFormulasModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), Preparation.class)
                .thenCompose(execute(EvaluateFormulas.class))
                .thenCompose(execute(EditCellFormulas.class))
                .thenCompose(execute(EvaluateFormulasWithExternalLinks.class))
                .get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(WorkingWithFormulasModule.class);
    }
}
