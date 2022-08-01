package eu.easyrpa.openframework.word;

import eu.easyrpa.openframework.core.utils.FilePathUtils;
import org.docx4j.Docx4J;
import org.docx4j.TraversalUtil;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.dml.Graphic;
import org.docx4j.dml.wordprocessingDrawing.Anchor;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.wml.*;

import javax.xml.bind.JAXBElement;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordDocument {

    private static final Logger LOGGER = Logger.getLogger(WordDocument.class.getName());
    /**
     * Path to related to this document Word file. It's a place where the document
     * is saved when method <code>save()</code> is called.
     */
    private String filePath;

    /**
     * Reference to related OPC Package.
     */
    private WordprocessingMLPackage opcPackage;

    /**
     * Creates empty Excel document.
     */
    public WordDocument() {
        reload(null);
    }

    /**
     * Creates new Excel document for specified input stream.
     *
     * @param is input stream that needs to accessed via this document.
     */
    public WordDocument(InputStream is) {
        reload(is);
    }

    public WordDocument(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null.");
        }
        try {
            setFilePath(file.getAbsolutePath());
            reload(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(String.format("File '%s' is not exist.", file.getAbsolutePath()), e);
        }
    }

    /**
     * Creates new Word document for specified path.
     *
     * @param path input path to Word file that needs to accessed via this document.
     * @throws IllegalArgumentException if <code>path</code> is <code>null</code> or not exist.
     */
    public WordDocument(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null.");
        }
        try {
            setFilePath(path.toAbsolutePath().toString());
            reload(Files.newInputStream(path));
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Failed to read file '%s'. Perhaps it's not exist.", path), e);
        }
    }

    public WordDocument(String filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null.");
        }
        File file = FilePathUtils.getFile(filePath);
        try {
            setFilePath(file.getAbsolutePath());
            reload(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(String.format("File '%s' is not exist.", filePath), e);
        }
    }

    /**
     * Gets file path to related to this Excel document file.
     *
     * @return the <code>filePath</code> if specified. Otherwise <code>null</code>.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Sets file path for this Excel document. The Excel document is saved to file defined by this path when
     * method <code>save()</code> is called.
     *
     * @param filePath the absolute path to file where excel document is saved when method <code>save()</code> is called.
     */
    public void setFilePath(String filePath) {
        this.filePath = FilePathUtils.normalizeFilePath(filePath);
    }

    /**
     * After performing the manipulations, the mandatory call to the save()
     * Overwrites the original file specified by <code>filePath</code> with actual content of this Word document.
     *
     * @throws NullPointerException if filePath missing. Try to use save(String filePath) and specify path.
     */

    public void save() {
        if (filePath != null) {
            saveAs(filePath);
        }
    }

    /**
     * After performing the manipulations, the mandatory call to the save()
     * Overwrites the original file specified by <code>filePath</code> with actual content of this Word document.
     *
     * @throws NullPointerException if filePath missing
     */

    public void save(String filePath) {
        saveAs(filePath);
    }

    /**
     * Saves this Word document to specified file.
     * <p>
     * Overwrites the content of specified file if it's exist and creates new one otherwise.
     * Also it will create all necessary parent folders if they do not exist either.
     *
     * @param filePath the path of the file to write.
     * @throws RuntimeException if specified file is a directory or cannot be written to.
     */

    public void saveAs(String filePath) {
        try {
            filePath = FilePathUtils.normalizeFilePath(filePath);
            File file = new File(filePath);
            if (!file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.getParentFile().mkdirs();
                if (!file.createNewFile()) {
                    throw new RuntimeException(String.format("Failed to create a new file at '%s'. Something went wrong.", filePath));
                }
            }
            try (FileOutputStream out = new FileOutputStream(file, false)) {
                opcPackage.save(out);
            }
        } catch (Docx4JException | IOException e) {
            throw new RuntimeException(String.format("Failed to save excel document to file located at '%s'.", filePath), e);
        }
    }

    public String text() {
        //TODO Implement this.
        // Reading of all text of this document.
        return null;
    }

    public List<Object> read() {
        List<Object> opcPackageObjList = opcPackage.getMainDocumentPart().getJaxbElement().getBody().getContent();
        List<Object> outputList = new ArrayList<>();
        for (Object o : opcPackageObjList) {
            ArrayListWml<?> listWml = (ArrayListWml<?>) ((P) o).getContent();
            List<R> runList = new ArrayList<>();
            for (Object o1 : listWml) {
                try {
                    R textRun = (R) o1;
                    if (textRun.getContent().isEmpty()) {
                        continue;
                    }
                    try {
                        Drawing pic = (Drawing) ((JAXBElement<?>) ((ArrayListWml<?>) textRun.getContent())
                                .get(pictureIndexValidation(textRun))).getValue();
                        ArrayListWml<?> drawingContent = (ArrayListWml<?>) pic.getAnchorOrInline();
                        Picture picture = new Picture(textRun);
                        try {
                            Anchor anchor = (Anchor) drawingContent.get(0);
                            Graphic anchGraphic = anchor.getGraphic();
                            picture.setBinaryImage(BinaryPartAbstractImage.getImage(opcPackage, anchGraphic));
                        } catch (ClassCastException e) {
                            Inline inline = (Inline) drawingContent.get(0);
                            Graphic inlineGraphic = inline.getGraphic();
                            picture.setBinaryImage(BinaryPartAbstractImage.getImage(opcPackage, inlineGraphic));
                        }
                        outputList.add(picture);
                    } catch (ClassCastException e) {
                        runList.add(textRun);
                    }
                } catch (ClassCastException e) {
                    continue;
                }
            }
            TextRange textRange = indexQualifier(runList);
            if (textRange.getStartIndex() != -1) {
                outputList.add(textRange);
            }
        }
        return outputList;


        //TODO Implement this.
        // Reading of all supported doc elements.
    }

    public void read(Function<Object, Boolean> handler) {
        //TODO Implement this.
        // Reading of all supported doc elements. Using via handler. If handler returns false
        // the reading is interrupted.
    }

    public void append(String text) {
        this.opcPackage.getMainDocumentPart().addParagraphOfText(text);

        //TODO Implement this.
        // Appends text the end of document.
        // append as new paragraph
    }

    public void append(Picture picture) throws Exception {
        BinaryPartAbstractImage imagePart =
                BinaryPartAbstractImage.createImagePart(opcPackage, Picture.convertFileToByteArray(picture.getPicFile()));
        Inline inline = imagePart.createImageInline("Default",
                "Default", 1, 2, false);
        this.opcPackage.getMainDocumentPart().addObject(Picture.addInlineImage(inline));
    }

    public void insertBefore(TextRange textRange, String text) {
        //TODO Implement this.
        // Insert text into the place related of text range.
        // Insert as new paragraph
    }

    public void insertBefore(TextRange textRange, Picture picture) {
        //TODO Implement this.
        // Insert picture into the place related of text range.
        // Insert as new paragraph
    }

    public void insertAfter(TextRange textRange, String text) {
        //TODO Implement this.
        // Insert text into the place related of text range.
        // Insert as new paragraph
    }

    public void insertAfter(TextRange textRange, Picture picture) {
        //TODO Implement this.
        // Insert picture into the place related of text range.
        // Insert as new paragraph
    }

    public void setVariable(String varName, String value) {
        //TODO Implement this.
        // Sets the value of specific variable in the document.
    }

    public void mapVariables(Map<String, String> values) throws Exception {
        VariablePrepare.prepare(opcPackage);
        opcPackage.getMainDocumentPart().variableReplace(values);
    }

    public TextRange findText(String regexp) {
        List<Object> elements = read();
        for (Object element : elements) {
            try {
                TextRange textRange = (TextRange) element;
                if (textRange.text().matches(regexp)) {
                    return textRange;
                }
            } catch (ClassCastException e) {
                break;
            }
        }
        return null;
    }

    public TextRange findText(String regexp, Function<TextRange, Boolean> checker) {
        //TODO Implement this.
        return null;
    }

    public List<TextRange> findAllText(String regexp) {
        //TODO Implement this.
        return null;
    }

    public Picture findPicture(String altTextRegexp) {
        //TODO Implement this.
        return null;
    }

    public Picture findPicture(String altTextRegexp, Function<Picture, Boolean> checker) {
        //TODO Implement this.
        return null;
    }

    public List<Picture> findPictures(String altTextRegexp) {
        //TODO Implement this.
        return null;
    }

    public void exportToPDF(String pdfFilePath) {
        try {
            HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
            htmlSettings.setOpcPackage(opcPackage);
            OutputStream os = new FileOutputStream(pdfFilePath);
            Docx4J.toPDF(opcPackage, os);
            os.flush();
            os.close();
        } catch (Docx4JException | FileNotFoundException e) {
            throw new IllegalArgumentException("Input stream missing or corrupted.", e);
        } catch (IOException e) {
            throw new RuntimeException("Cannot close output stream.", e);
        }
    }

    public void exportToPDF(Path pdfFilePath) {
        exportToPDF(pdfFilePath.toAbsolutePath().toString());
    }

    /**
     * Gets this base docx4j model element (WordprocessingMLPackage).
     *
     * @return the instance of WordprocessingMLPackage.
     */
    public WordprocessingMLPackage getOpcPackage() {
        return opcPackage;
    }

    private TextRange indexQualifier(List<R> runList) throws IndexOutOfBoundsException {
        if (!runList.isEmpty()) {
            int startIndex = 0;
            int endIndex = 0;
            Pattern startPattern = Pattern.compile("[^\\s.*;:()#'\\/]");
            try {
                Text startTextOfRun = (Text) ((JAXBElement<?>) ((ArrayListWml<?>) runList
                        .get(startIndexValidation(runList, false)).getContent())
                        .get(startIndexValidation(runList, true))).getValue();
                Matcher firstMatcher = startPattern.matcher(startTextOfRun.getValue());
                if (firstMatcher.find()) {
                    startIndex = firstMatcher.start();
                }
                Text endTextOfRun = (Text) ((JAXBElement<?>) ((ArrayListWml<?>) runList
                        .get(endIndexValidation(runList, false)).getContent())
                        .get(endIndexValidation(runList, true))).getValue();
                Pattern endPattern = Pattern.compile("[^\\s,*;:#'\\\\](?=[\\s*;:,#'\\\\]*$)");
                Matcher secondMatcher = endPattern.matcher(endTextOfRun.getValue());
                if (secondMatcher.find()) {
                    endIndex = secondMatcher.end();
                }
                return new TextRange(runList, startIndex, endIndex);
            } catch (IndexOutOfBoundsException e) {
                return new TextRange(runList, -1, -1);
            }
        }
        return new TextRange(runList, -1, -1);
    }

    private int startIndexValidation(List<R> runList, boolean isNestedContent) {
        for (int i = 0; i < runList.size(); i++) {
            for (int j = 0; j < runList.get(i).getContent().size(); j++) {
                try {
                    Text.class.cast(((JAXBElement<?>) ((ArrayListWml<?>) runList.get(i).getContent()).get(j)).getValue());
//                    Text startTextOfRun = (Text) ((JAXBElement<?>) ((ArrayListWml<?>) runList.get(i).getContent()).get(j)).getValue();
                    if (isNestedContent) {
                        return j;
                    } else {
                        return i;
                    }
                } catch (ClassCastException e) {
                    continue;
                }
            }
        }
        return -1;
    }

    private int endIndexValidation(List<R> runList, boolean isNestedContent) {
        for (int i = runList.size() - 1; i >= 0; i--) {
            for (int j = 0; j < runList.get(i).getContent().size(); j++) {
                try {
                    Text.class.cast(((JAXBElement<?>) ((ArrayListWml<?>) runList.get(i).getContent()).get(j)).getValue());
//                    Text endTextOfRun = (Text) ((JAXBElement<?>) ((ArrayListWml<?>) runList.get(i).getContent()).get(j)).getValue();
                    if (isNestedContent) {
                        return j;
                    } else {
                        return i;
                    }
                } catch (ClassCastException e) {
                    continue;
                }
            }
        }
        return -1;
    }

    private int pictureIndexValidation(R run) {
        for (int j = 0; j < run.getContent().size(); j++) {
            try {
                Drawing.class.cast(((JAXBElement<?>) ((ArrayListWml<?>) run.getContent()).get(j)).getValue());
//                Drawing pic = (Drawing) ((JAXBElement<?>) ((ArrayListWml<?>) run.getContent()).get(j)).getValue();
                return j;
            } catch (ClassCastException e) {
                continue;
            }
        }
        return 0;
    }

//
//
//
//    //?
//    public Object findElement(Class<?> toSearch) {
////        TraversalUtil.visit(wordPackage, true, new TraversalUtilVisitor<P>(){
////            public void apply(P element) {
////
////            }
////        });
//
//        for (Object o : getElements()) {
//            try {
//                return toSearch.cast(o);
//            } catch (ClassCastException e) {
//                return null;
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Helper recursive method to find any List elements of object by docx4j hierarchy.
//     *
//     * @param obj      the element in which to find the nested search elements.
//     * @param toSearch class which you want to find. For example <code>{Tbl.class}, {Tr.class}, {P.class}</code>
//     * @return the nested List of elements by provided base search element.
//     */
//    public List<Object> getElements(Object obj, Class<?> toSearch) {
//        List<Object> result = new ArrayList<>();
//        if (obj instanceof JAXBElement) obj = ((JAXBElement<?>) obj).getValue();
//
//        if (obj.getClass().equals(toSearch))
//            result.add(obj);
//        else if (obj instanceof ContentAccessor) {
//            List<?> children = ((ContentAccessor) obj).getContent();
//            for (Object child : children) {
//                result.addAll(getElements(child, toSearch));
//            }
//        }
//        return result;
//    }
//
//    /**
//     * Gets elements which are in the Word file.
//     *
//     * @return List of elements.
//     */
//    public List<Object> getElements() {
//        List<Object> objectList = new ArrayList<>();
//        for (Object o : opcPackage.getMainDocumentPart().getJaxbElement().getBody().getContent()) {
//            try {
//                JAXBElement jaxbElement = (JAXBElement) o;
//                objectList.add(jaxbElement.getValue());
//            } catch (ClassCastException e) {
//                objectList.add(o);
//            }
//        }
//        return objectList;
//    }
//
//    //test method (to dev)
//    public List<Class<?>> getClassElements() {
//        List<Class<?>> outputList = new ArrayList<>();
//        List<Object> objectList = opcPackage.getMainDocumentPart().getJaxbElement().getBody().getContent();
//        for (Object o : objectList) {
//            outputList.add(o.getClass());
//        }
//        return outputList;
//    }
//
//    /**
//     * Finds the table with a cell that contains given value.
//     *
//     * @param matchMethod method that defines how passed value are matched with each cell value.
//     * @param value       string value to match.
//     * @return instance of found table or <code>null</code> if table not found.
//     * @see MatchMethod
//     */
//    public Tbl findTable(MatchMethod matchMethod, String value) {
//
//
//        for (Object o : getElements()) {
//            try {
//                Tbl tbl = (Tbl) o;
//                List<Object> rows = getElements(tbl, Tr.class);
//                for (Object row : rows) {
//                    Tr templateRow = (Tr) row;
//                    List<Object> rowsContent = templateRow.getContent();
//                    for (Object rowContent : rowsContent) {
//                        JAXBElement rowElement = (JAXBElement) rowContent;
//                        Tc tc = (Tc) rowElement.getValue();
//                        ArrayListWml paragraphsList = (ArrayListWml) tc.getContent();
//                        P p = (P) paragraphsList.get(0);
//                        ArrayListWml rList = (ArrayListWml) p.getContent();
//                        if (rList.isEmpty()) {
//                            break;
//                        }
//                        R r = (R) rList.get(0);
//                        ArrayListWml textList = (ArrayListWml) r.getContent();
//                        JAXBElement textElement = (JAXBElement) textList.get(0);
//                        Text sourceValue = (Text) textElement.getValue();
//                        if (matchMethod.match(sourceValue.getValue(), value)) {
//                            return tbl;
//                        }
//                    }
//                }
//            } catch (ClassCastException e) {
//                continue;
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Finds the paragraph with given text value.
//     *
//     * @param matchMethod matchMethod that defines how passed value are matched with each paragraph value.
//     * @param value       string value to match.
//     * @return instance of found paragraph.
//     * @see MatchMethod
//     */
//    public P findParagraphByText(MatchMethod matchMethod, String value) {
//        for (Object o : getElements()) {
//            try {
//                P p = (P) o;
//                ArrayListWml rList = (ArrayListWml) p.getContent();
//                if (rList.isEmpty()) {
//                    return null;
//                }
//                R r = (R) rList.get(0);
//                ArrayListWml textList = (ArrayListWml) r.getContent();
//                JAXBElement textElement = (JAXBElement) textList.get(0);
//                Text sourceValue = (Text) textElement.getValue();
//                if (matchMethod.match(sourceValue.getValue(), value)) {
//                    return p;
//                }
//            } catch (ClassCastException e) {
//                continue;
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Finds the any object with given text value.
//     *
//     * @param matchMethod matchMethod that defines how passed value are matched with element's text value.
//     * @param value       string value to match.
//     * @return instance of element.
//     * E.g. This method can return any element. This means that after calling the method,
//     * you can cast the result to any element of any class that is contained in docx4j hierarchy.
//     * <code>{Tbl table = (Tbl) returnedObject}</code>
//     * @throws ClassCastException if you cast different classes. E.g. <code>{Tr tr = (Tr) returnedValue}</code>
//     *                            but <code>{returnedValue}</code> can be cast only to <code>{P paragraph = (P) returnedValue}</code>
//     * @see MatchMethod
//     */
//    public Object findElementByText(MatchMethod matchMethod, String value) {
//        try {
//            return findParagraphByText(matchMethod, value);
//        } catch (ClassCastException e) {
//            for (Object o : getElements()) {
//                try {
//                    Tbl tbl = (Tbl) o;
//                    List<Object> rows = getElements(tbl, Tr.class);
//                    for (Object row : rows) {
//                        Tr templateRow = (Tr) row;
//                        List<Object> rowsContent = templateRow.getContent();
//                        for (Object rowContent : rowsContent) {
//                            JAXBElement rowElement = (JAXBElement) rowContent;
//                            Tc tc = (Tc) rowElement.getValue();
//                            ArrayListWml paragraphsList = (ArrayListWml) tc.getContent();
//                            P p = (P) paragraphsList.get(0);
//                            ArrayListWml rList = (ArrayListWml) p.getContent();
//                            if (rList.isEmpty()) {
//                                break;
//                            }
//                            R r = (R) rList.get(0);
//                            ArrayListWml textList = (ArrayListWml) r.getContent();
//                            JAXBElement textElement = (JAXBElement) textList.get(0);
//                            Text sourceValue = (Text) textElement.getValue();
//                            if (matchMethod.match(sourceValue.getValue(), value)) {
//                                return tc;
//                            }
//                        }
//                    }
//                } catch (ClassCastException exception) {
//                    continue;
//                }
//            }
//        }
//        return null;
//    }

    /**
     * Creates and set word package from input stream specified.
     *
     * @param is input stream with word package contents. Creates new word package if is is null.
     */
    public void reload(InputStream is) {
        try {
            if (is == null) {
                opcPackage = WordprocessingMLPackage.createPackage();
                // Create new one
            } else {
                opcPackage = WordprocessingMLPackage.load(is);
            }

        } catch (Exception e) {
            throw new RuntimeException(String.format("Initializing of word document for main part '%s' has failed.", getFilePath()), e);
        }
    }
}


//implement getElements(), findTable(), findParagraphByText(), findElementByText(), findElement(Class class), find() get() remove(WordDocEl)

//    private <T> T perform(BiFunction<Object, Class<?>, T> function) {
//        return function.apply(wordPackage.getMainDocumentPart(), P.class);
//    }
//
//    private List<?> getElements(BiFunction<Object, Class<?>, List<?>> function) {
//        List<Object> list = new ArrayList<>();
//        return perform((obj, aClass) -> {
//            if (obj instanceof JAXBElement) obj = ((JAXBElement<?>) obj).getValue();
//            if (obj.getClass().equals(aClass))
//                list.add(obj);
//            else if (obj instanceof ContentAccessor) {
//                List<?> children = ((ContentAccessor) obj).getContent();
//                list.addAll(children);
//            }
//            return function.apply(obj, aClass);
//        });
//    }

