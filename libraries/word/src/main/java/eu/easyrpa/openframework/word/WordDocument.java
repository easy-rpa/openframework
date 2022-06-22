package eu.easyrpa.openframework.word;

import eu.easyrpa.openframework.core.utils.FilePathUtils;
import eu.easyrpa.openframework.word.constants.MatchMethod;
import eu.easyrpa.openframework.word.internal.docx4j.Docx4jElementsCache;
import org.docx4j.model.PropertyResolver;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.*;

import javax.xml.bind.JAXBElement;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class WordDocument {

    /**
     * Unique Word document identified.
     */
    private int id = -1;

    private WordDocumentElement child;

    /**
     * Path to related to this document Word file. It's a place where the document
     * is saved when method <code>save()</code> is called.
     */
    private String filePath;

    /**
     * Reference to related Docx4j document.
     */
    private WordprocessingMLPackage wordPackage;

    private List<WordDocumentElement> wordDocumentElements;

    /**
     * Creates empty Excel document.
     */
    public WordDocument() {
        initMainDocumentPart(null);
    }

    /**
     * Creates new Excel document for specified input stream.
     *
     * @param is input stream that needs to accessed via this document.
     */
    public WordDocument(InputStream is) {
        initMainDocumentPart(is);
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
            setFilePath(String.valueOf(path.toAbsolutePath()));
            initMainDocumentPart(new FileInputStream(path.toFile()));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(String.format("File '%s' is not exist.", path.toAbsolutePath()), e);
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
                wordPackage.save(out);
            }
        } catch (Docx4JException | IOException e) {
            throw new RuntimeException(String.format("Failed to save excel document to file located at '%s'.", filePath), e);
        }
    }

    /**
     * Updates content of this Excel document. Invokes Apache POI workbook reinitialization.
     *
     * @param is input stream with contents.
     */

    public void update(InputStream is) {
        initMainDocumentPart(is);
    }

    /**
     * Gets this Word document unique identifier.
     *
     * @return unique identifier of this document.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets this base docx4j model element (WordprocessingMLPackage).
     *
     * @return the instance of WordprocessingMLPackage.
     */
    public WordprocessingMLPackage getWordPackage() {
        return wordPackage;
    }

    /**
     * Gets list of WordDocumentElements.
     *
     * @return the list of WordDocumentElements.
     */
    public List<WordDocumentElement> getWordDocumentElements() {
        return wordDocumentElements;
    }

    /**
     * Helper method to add new instance of WordDocumentElement into list.
     *
     * @param wordDocumentElement the instance of WordDocumentElement.
     */
    public void addWordDocumentElement(WordDocumentElement wordDocumentElement) {
        getWordDocumentElements().add(wordDocumentElement);
    }

    /**
     * Helper functional method which return a setting PropertyResolver instance.
     * That class works out the actual set of properties which apply, following the order specified in ECMA-376.
     *
     * @return property resolver instance.
     */
    public PropertyResolver getPropertyResolver() {
        return this.wordPackage.getMainDocumentPart().getPropertyResolver();
    }

    //?
    public Object findElement(Class<?> toSearch) {
        for (Object o : getElements()) {
            try {
                return toSearch.cast(o);
            } catch (ClassCastException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Helper recursive method to find any List elements of object by docx4j hierarchy.
     *
     * @param obj      the element in which to find the nested search elements.
     * @param toSearch class which you want to find. For example <code>{Tbl.class}, {Tr.class}, {P.class}</code>
     * @return the nested List of elements by provided base search element.
     */
    public List<Object> getElements(Object obj, Class<?> toSearch) {
        List<Object> result = new ArrayList<>();
        if (obj instanceof JAXBElement) obj = ((JAXBElement<?>) obj).getValue();

        if (obj.getClass().equals(toSearch))
            result.add(obj);
        else if (obj instanceof ContentAccessor) {
            List<?> children = ((ContentAccessor) obj).getContent();
            for (Object child : children) {
                result.addAll(getElements(child, toSearch));
            }
        }
        return result;
    }

    /**
     * Gets elements which are in the Word file.
     *
     * @return List of elements.
     */
    public List<Object> getElements() {
        List<Object> objectList = new ArrayList<>();
        for (Object o : wordPackage.getMainDocumentPart().getJaxbElement().getBody().getContent()) {
            try {
                JAXBElement jaxbElement = (JAXBElement) o;
                objectList.add(jaxbElement.getValue());
            } catch (ClassCastException e) {
                objectList.add(o);
            }
        }
        return objectList;
    }

    //test method (to dev)
    public List<Class<?>> getClassElements() {
        List<Class<?>> outputList = new ArrayList<>();
        List<Object> objectList = wordPackage.getMainDocumentPart().getJaxbElement().getBody().getContent();
        for (Object o : objectList) {
            outputList.add(o.getClass());
        }
        return outputList;
    }

    /**
     * Finds the table with a cell that contains given value.
     *
     * @param matchMethod method that defines how passed value are matched with each cell value.
     * @param value       string value to match.
     * @return instance of found table or <code>null</code> if table not found.
     * @see MatchMethod
     */
    public Tbl findTable(MatchMethod matchMethod, String value) {
        for (Object o : getElements()) {
            try {
                Tbl tbl = (Tbl) o;
                List<Object> rows = getElements(tbl, Tr.class);
                for (Object row : rows) {
                    Tr templateRow = (Tr) row;
                    List<Object> rowsContent = templateRow.getContent();
                    for (Object rowContent : rowsContent) {
                        JAXBElement rowElement = (JAXBElement) rowContent;
                        Tc tc = (Tc) rowElement.getValue();
                        ArrayListWml paragraphsList = (ArrayListWml) tc.getContent();
                        P p = (P) paragraphsList.get(0);
                        ArrayListWml rList = (ArrayListWml) p.getContent();
                        if (rList.isEmpty()) {
                            break;
                        }
                        R r = (R) rList.get(0);
                        ArrayListWml textList = (ArrayListWml) r.getContent();
                        JAXBElement textElement = (JAXBElement) textList.get(0);
                        Text sourceValue = (Text) textElement.getValue();
                        if (matchMethod.match(sourceValue.getValue(), value)) {
                            return tbl;
                        }
                    }
                }
            } catch (ClassCastException e) {
                continue;
            }
        }
        return null;
    }

    /**
     * Finds the paragraph with given text value.
     *
     * @param matchMethod matchMethod that defines how passed value are matched with each paragraph value.
     * @param value       string value to match.
     * @return instance of found paragraph.
     * @see MatchMethod
     */
    public P findParagraphByText(MatchMethod matchMethod, String value) {
        for (Object o : getElements()) {
            try {
                P p = (P) o;
                ArrayListWml rList = (ArrayListWml) p.getContent();
                if (rList.isEmpty()) {
                    return null;
                }
                R r = (R) rList.get(0);
                ArrayListWml textList = (ArrayListWml) r.getContent();
                JAXBElement textElement = (JAXBElement) textList.get(0);
                Text sourceValue = (Text) textElement.getValue();
                if (matchMethod.match(sourceValue.getValue(), value)) {
                    return p;
                }
            } catch (ClassCastException e) {
                continue;
            }
        }
        return null;
    }

    /**
     * Finds the any object with given text value.
     *
     * @param matchMethod matchMethod that defines how passed value are matched with element's text value.
     * @param value       string value to match.
     * @return instance of element.
     * E.g. This method can return any element. This means that after calling the method,
     * you can cast the result to any element of any class that is contained in docx4j hierarchy.
     * <code>{Tbl table = (Tbl) returnedObject}</code>
     * @throws ClassCastException if you cast different classes. E.g. <code>{Tr tr = (Tr) returnedValue}</code>
     *                            but <code>{returnedValue}</code> can be cast only to <code>{P paragraph = (P) returnedValue}</code>
     * @see MatchMethod
     */
    public Object findElementByText(MatchMethod matchMethod, String value) {
        try {
            return findParagraphByText(matchMethod, value);
        } catch (ClassCastException e) {
            for (Object o : getElements()) {
                try {
                    Tbl tbl = (Tbl) o;
                    List<Object> rows = getElements(tbl, Tr.class);
                    for (Object row : rows) {
                        Tr templateRow = (Tr) row;
                        List<Object> rowsContent = templateRow.getContent();
                        for (Object rowContent : rowsContent) {
                            JAXBElement rowElement = (JAXBElement) rowContent;
                            Tc tc = (Tc) rowElement.getValue();
                            ArrayListWml paragraphsList = (ArrayListWml) tc.getContent();
                            P p = (P) paragraphsList.get(0);
                            ArrayListWml rList = (ArrayListWml) p.getContent();
                            if (rList.isEmpty()) {
                                break;
                            }
                            R r = (R) rList.get(0);
                            ArrayListWml textList = (ArrayListWml) r.getContent();
                            JAXBElement textElement = (JAXBElement) textList.get(0);
                            Text sourceValue = (Text) textElement.getValue();
                            if (matchMethod.match(sourceValue.getValue(), value)) {
                                return tc;
                            }
                        }
                    }
                } catch (ClassCastException exception) {
                    continue;
                }
            }
        }
        return null;
    }

    /**
     * Creates and set word package from input stream specified.
     *
     * @param is input stream with word package contents. Creates new word package if is is null.
     */
    private void initMainDocumentPart(InputStream is) {
        try {
            if (is == null) {
                wordPackage = WordprocessingMLPackage.createPackage();
                // Create new one
            } else {
                wordPackage = WordprocessingMLPackage.load(is);
            }


            if (id > 0) {
                Docx4jElementsCache.unregister(id);
            } else {
                id = Docx4jElementsCache.generateWordDocumentId();
            }

            Docx4jElementsCache.register(id, wordPackage);
            this.wordDocumentElements = new ArrayList<>();
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

