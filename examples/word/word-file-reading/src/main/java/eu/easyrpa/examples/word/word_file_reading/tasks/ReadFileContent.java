package eu.easyrpa.examples.word.word_file_reading.tasks;

import eu.easyrpa.openframework.word.WordDocument;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;

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
//        TextRange text = doc.findText(regx)
//        TextRange text = doc.findText(regx, o->{
//            return true // return as result
//            return false // continue
//        })
//        List<TextRange> result = findAllText(regx)
//
//       // TextRange - the work within current paragraph
//        doc.findText(regx).format().bold().color(red);
//        doc.findText(regx).expandLeft().expandRight().replace(text)
//        doc.findText(regx).text()
//        doc.findText(regx).words()
//        doc.findText(regx).addAfter(text)
//        doc.findText(regx).addBefore(path) //insert image
//        doc.findText(regx).replaceWith(text)
//        doc.findText(regx).replaceWith(picture) //insert image
//        doc.findText(regx).remove()
//
//       // TextFormat
//        format().bold()
//        format().italic()
//        format().font()
//        format().fontSize()
//        format().color()
//        format().background()
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
