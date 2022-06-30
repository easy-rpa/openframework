package eu.easyrpa.examples.word.word_file_reading.tasks;

import eu.easyrpa.openframework.word.WordDocElement;
import eu.easyrpa.openframework.word.WordDocument;
import eu.easyrpa.openframework.word.constants.MatchMethod;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ApTaskEntry(name = "Read File Content")
@Slf4j
public class ReadFileContent extends ApTask {

    @Configuration(value = "source.document.file")
    private String sourceDocFile;

    @Override
    public void execute() {
        log.info("Read the content of document located at: {}", sourceDocFile);
        WordDocument doc = new WordDocument(sourceDocFile);

//        //Text
//        String text = doc.text(); // read all text
//        List<WordDocElement> list = doc.read();
//        doc.read(w->{ // iterate over all parts
//            return false;
////            TextRange // for each paragraph
////            Picture
////            Table
//        });
//
//        doc.append(text); //add paragraph the end
//        doc.setVariable(vVarName, value); // set var value
//        doc.mapVariables(Map<var, value>);
//        TextRange pos = doc.findText(regx)
//        doc.findText(regx, o->{
//            return true // go to next
//            return false // stop
//        })
//        List<TextRange> result = findAllText(regx)
//
//       // TextRange - the work within current paragraph
//        doc.findText(regx).style().bold().color(red).apply();
//        doc.findText(regx).extend(left|right|both, 1, words|chars|lines).replace(text)
//        doc.findText(regx).text()
//        doc.findText(regx).words()
//        doc.findText(regx).add(after|before, text)
//        doc.findText(regx).add(after|before, path) //insert image
//        doc.findText(regx).add(after|before, path) // insert table
//        doc.findText(regx).replace(text)
//        doc.findText(regx).replaceWith(picture|file|path) //insert image
//        doc.findText(regx).replaceWith(entityClass, List<Entiry>) // insert table
//        doc.findText(regx).remove()
//
//       // TextStyle
//        style().bold()
//        style().italic()
//        style().font()
//        style().fontSize()
//        style().color()
//        style().background()
//
//       // Picture
//        doc.append(picture) // add picture to the end
//        Picture p = doc.findPicture(altTextRegx)
//        doc.findPicture(altTextRegx, p->{
//            return true // go to next
//            return false // stop
//        })
//        doc.findPicture(altTextRegx).replace(newPicture)
//
//        // Table
//        doc.append(entityClass, List<Entiry>)
//
//        table.rowsCount()
//        table.columnsCount()
//        table.readCell()
//        table.addRow()
//
//        doc.exportToPDF()
//        doc.save()
//        doc.saveAs()
//
//        doc.findParagraphByText(MatchMethod.CONTAINS, "test");
    }
}
