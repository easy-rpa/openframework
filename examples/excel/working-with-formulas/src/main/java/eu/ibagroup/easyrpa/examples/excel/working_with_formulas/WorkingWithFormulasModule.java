package eu.ibagroup.easyrpa.examples.excel.working_with_formulas;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.examples.excel.sheets_manipulating.tasks.*;
import eu.ibagroup.easyrpa.examples.excel.working_with_formulas.tasks.EvaluateFormulas;
import eu.ibagroup.easyrpa.examples.excel.working_with_formulas.tasks.EvaluateFormulasWithExternalLinks;
import eu.ibagroup.easyrpa.examples.excel.working_with_formulas.tasks.EditCellFormulas;
import eu.ibagroup.easyrpa.examples.excel.working_with_rows.tasks.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Working with Formulas")
public class WorkingWithFormulasModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), EditCellFormulas.class)
                .thenCompose(execute(EvaluateFormulas.class))
                .thenCompose(execute(EvaluateFormulasWithExternalLinks.class))
                .get();
    }
}
