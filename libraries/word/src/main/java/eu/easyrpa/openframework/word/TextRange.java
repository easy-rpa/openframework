package eu.easyrpa.openframework.word;

import eu.easyrpa.openframework.word.util.Docx4jUtils;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.wml.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextRange {

    public static final String EXPAND_RIGHT = "(?<=\\s).";
    private static final String EXPAND_LEFT = ".+(?=\\s)";

    private List<R> textRuns;

    private int startIndex;

    private int endIndex;

    private int expandIndex;

    TextRange(List<R> textRuns, int startIndex, int endIndex) {
        this.textRuns = textRuns;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public TextFormat format() {
        return new TextFormat(this);
    }

    public String text() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < textRuns.size(); i++) {
            if (textRuns.size() == 1 && textRuns.get(i).getContent().size() == 1) {
                Text singleText = Docx4jUtils.getText(textRuns, i);
                if (singleText != null) {
                    sb.append(singleText.getValue(), startIndex, endIndex);
                }
            } else {
                if (i == 0) {
                    Text startText = Docx4jUtils.getText(textRuns, i);
                    if (startText != null) {
                        sb.append(startText.getValue().substring(startIndex));
                    }
                } else {
                    Text mainText = Docx4jUtils.getText(textRuns, i);
                    if (mainText != null) {
                        sb.append(mainText.getValue());
                    }
                }
            }
        }
        return sb.toString();
    }

    public List<TextRange> words() {
        // todo ask for returned result _>
        //TODO Implement this.
        // Split into words.
        return null;
    }

    public void words(Function<TextRange, Boolean> handler) {
        //TODO Implement this.
        // Split into words and iterate over them as text ranges. If handler returns false
        // the iterating is interrupted.
    }


    public TextRange subRange(String startRegexp, String endRegexp) {
        String textOfRange = text();
        int subStartIndex = startIndex;
        int subEndIndex = endIndex;
        if (startRegexp != null) {
            Pattern startRegexPattern = Pattern.compile(startRegexp);
            Matcher startRegexMatcher = startRegexPattern.matcher(textOfRange);
            if (startRegexMatcher.find()) {
                subStartIndex = startRegexMatcher.start();
            }
        }
        if (endRegexp != null) {
            Pattern endRegexPattern = Pattern.compile(endRegexp);
            Matcher endRegexMatcher = endRegexPattern.matcher(textOfRange);
            if (endRegexMatcher.find()) {
                subEndIndex = endRegexMatcher.end() - subStartIndex;
            }
        }
        int newSubStartIndex = 0;
        List<R> subRunList = new ArrayList<>(textRuns);
        List<R> subEndRunList = new ArrayList<>();
        for (int i = 0; i < textRuns.size(); i++) {
            if (subStartIndex >= 0) {
                if (Docx4jUtils.getText(textRuns, i) != null) {
                    Text castedText = Objects.requireNonNull(Docx4jUtils.getText(textRuns, i));
                    subStartIndex = subStartIndex - castedText.getValue().length();
                    if (subStartIndex > 0) {
                        subRunList.remove(castedText.getParent());
                    }
                    newSubStartIndex = castedText.getValue().length();
                } else {
                    continue;
                }
            } else {
                newSubStartIndex = newSubStartIndex + subStartIndex;
                break;
            }
        }
        for (int i = 0; i < subRunList.size(); i++) {
            if (subEndIndex >= 0) {
                if (Docx4jUtils.getText(subRunList, i) != null) {
                    Text text = Docx4jUtils.getText(subRunList, i);
                    subEndIndex = subEndIndex - text.getValue().length();
                } else {
                    continue;
                }
            }
            if (subEndIndex <= 0) {
                Text text = Objects.requireNonNull(Docx4jUtils.getText(subRunList, i));
                subEndIndex = text.getValue().length() - 1;
                subEndRunList = subRunList.subList(0, i + 1);
                break;
            }
        }
        return new TextRange(subEndRunList, newSubStartIndex, subEndIndex);
    }


    public boolean expandLeft() {
        Pattern pattern = Pattern.compile(EXPAND_LEFT);
        try {
            String text = Objects.requireNonNull(Docx4jUtils.getText(textRuns, expandIndex)).getValue();
            if (text.length() == 1) {
                startIndex = text.length();
                expandIndex--;
                return true;
            }
            String subtext = text.substring(0, startIndex - 1);
            Matcher matcher = pattern.matcher(subtext);
            if (matcher.find()) {
                startIndex = startIndex - (matcher.end() + 1);
            } else {
                expandIndex--;
            }
            return true;
        } catch (NullPointerException e) {
            expandIndex--;
            return true;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public boolean expandRight() {
        Pattern pattern = Pattern.compile(EXPAND_RIGHT);
        try {
            String text = Objects.requireNonNull(Docx4jUtils.getText(textRuns, expandIndex)).getValue();
            if (text.length() == 1) {
                startIndex = 0;
                expandIndex++;
                return true;
            }
            String subtext = text.substring(startIndex);
            Matcher matcher = pattern.matcher(subtext);
            if (matcher.find()) {
                startIndex = startIndex + matcher.start();
            } else {
                expandIndex++;
            }
            return true;
        } catch (NullPointerException e) {
            expandIndex++;
            return true;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public void addBefore(String text) {
        if (textRuns.get(0).getParent() instanceof P) {
            P parentP = (P) textRuns.get(0).getParent();
            R run = Docx4jUtils.createRun(text, parentP);
            parentP.getContent().add(Math.max(expandIndex - 1, 0), run);
            textRuns.add(run);
            startIndex = startIndex - text.length();
        }
    }

    public void addBefore(Picture picture, WordprocessingMLPackage opcPackage) throws Exception {
        BinaryPartAbstractImage imagePart =
                BinaryPartAbstractImage.createImagePart(opcPackage, Docx4jUtils.convertFileToByteArray(picture.getPicFile()));
        Inline inline = imagePart.createImageInline("Default",
                "Default", 1, 2, false);
        int index = opcPackage.getMainDocumentPart().getJaxbElement().getBody().getContent().indexOf(textRuns.get(0).getParent());
        opcPackage.getMainDocumentPart().getJaxbElement().getBody().getContent().add(Math.max(index - 1, 0), Docx4jUtils.addInlineImage(inline));
    }

    public void addAfter(String text) {
        if (textRuns.get(0).getParent() instanceof P) {
            P parentP = (P) textRuns.get(0).getParent();
            R run = Docx4jUtils.createRun(text, parentP);
            parentP.getContent().add(run);
            textRuns.add(run);
            endIndex = endIndex + text.length();
        }
    }

    public void addAfter(Picture picture, WordprocessingMLPackage opcPackage) throws Exception {
        BinaryPartAbstractImage imagePart =
                BinaryPartAbstractImage.createImagePart(opcPackage, Docx4jUtils.convertFileToByteArray(picture.getPicFile()));
        Inline inline = imagePart.createImageInline("Default",
                "Default", 1, 2, false);
        opcPackage.getMainDocumentPart().getJaxbElement().getBody().getContent().add(Docx4jUtils.addInlineImage(inline));
    }

    public void replaceWith(String text) {
        if(textRuns.get(0).getParent() instanceof P) {
            P parent = (P) textRuns.get(0).getParent();
            R run = Docx4jUtils.createRun(text, parent);
            int index = parent.getContent().indexOf(textRuns.get(expandIndex));
            if(index != -1) {
                parent.getContent().set(index, run);
            }
            textRuns.set(expandIndex, run);
        }

    }

    public void replaceWith(Picture picture, WordprocessingMLPackage opcPackage) throws Exception {
        BinaryPartAbstractImage imagePart =
                BinaryPartAbstractImage.createImagePart(opcPackage, Docx4jUtils.convertFileToByteArray(picture.getPicFile()));
        Inline inline = imagePart.createImageInline("Default",
                "Default", 1, 2, false);
        int index = opcPackage.getMainDocumentPart().getJaxbElement().getBody().getContent().indexOf(textRuns.get(0).getParent());
        opcPackage.getMainDocumentPart().getJaxbElement().getBody().getContent().set(index, Docx4jUtils.addInlineImage(inline));
    }

    public void remove() {
        textRuns.remove(expandIndex);
    }

    public List<R> getTextRuns() {
        return textRuns;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public int getExpandIndex() {
        return expandIndex;
    }

    public void setExpandIndex(int expandIndex) {
        this.expandIndex = expandIndex;
    }


//    private Map<String, Object> subValidation(int subStartIndex, List<R> rList) {
//        Map<String, Object> map = new HashMap<>();
//        List<R> subRunList = new ArrayList<>(rList);
//        int newSubStartIndex = 0;
//        for (int i = 0; i < rList.size(); i++) {
//            if (subStartIndex >= 0) {
//                Text castedText = Objects.requireNonNull(textCast(i));
//                subStartIndex = subStartIndex - castedText.getValue().length();
//                if (subStartIndex > 0) {
//                    subRunList.remove(castedText.getParent());
//                }
//                newSubStartIndex = castedText.getValue().length();
//            } else {
//                newSubStartIndex = newSubStartIndex + subStartIndex;
////                subRunList.add(textRuns.get(i));
//                break;
//            }
//        }
//        map.put("runList", subRunList);
//        map.put("index", newSubStartIndex);
//        return map;
//    }

}
