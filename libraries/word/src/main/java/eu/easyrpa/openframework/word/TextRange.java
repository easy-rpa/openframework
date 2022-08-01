package eu.easyrpa.openframework.word;

import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.wml.*;

import javax.xml.bind.JAXBElement;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        //TODO implement this
        return new TextFormat(this);
    }

    public String text() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < textRuns.size(); i++) {
            if (textRuns.size() == 1 && textRuns.get(i).getContent().size() == 1) {
                Text singleText = textCast(textRuns, i);
                if (singleText != null) {
                    sb.append(singleText.getValue(), startIndex, endIndex);
                }
            } else {
                if (i == 0) {
                    Text startText = textCast(textRuns, i);
                    if (startText != null) {
                        sb.append(startText.getValue().substring(startIndex));
                    }
                } else {
                    Text mainText = textCast(textRuns, i);
                    if (mainText != null) {
                        sb.append(mainText.getValue());
                    }
                }
            }
        }
        return sb.toString();
    }

    public List<TextRange> words() {
//        List<String> wordsList = Arrays.stream(text().split(" ")).collect(Collectors.toList());
//        List<TextRange> ranges = new ArrayList<>();
//        wordsList.stream().forEach(s -> {
//            ranges.add(new TextRange())
//        });

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
                try {
                    Text castedText = Objects.requireNonNull(textCast(textRuns, i));
                    subStartIndex = subStartIndex - castedText.getValue().length();
                    if (subStartIndex > 0) {
                        subRunList.remove(castedText.getParent());
                    }
                    newSubStartIndex = castedText.getValue().length();
                } catch (NullPointerException e) {
                    continue;
                }
            } else {
                newSubStartIndex = newSubStartIndex + subStartIndex;
                break;
            }
        }
        for (int i = 0; i < subRunList.size(); i++) {
            if (subEndIndex >= 0) {
                try {
                    Text castedText = Objects.requireNonNull(textCast(subRunList, i));
                    subEndIndex = subEndIndex - castedText.getValue().length();
                } catch (NullPointerException e) {
                    continue;
                }
            }
            if (subEndIndex <= 0) {
                Text castedText = Objects.requireNonNull(textCast(subRunList, i));
                subEndIndex = castedText.getValue().length() - 1;
                subEndRunList = subRunList.subList(0, i + 1);
                break;
            }
        }
        return new TextRange(subEndRunList, newSubStartIndex, subEndIndex);
    }


    public boolean expandLeft() {
        Pattern pattern = Pattern.compile(EXPAND_LEFT);
        try {
            String text = Objects.requireNonNull(textCast(textRuns, expandIndex)).getValue();
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
            String text = Objects.requireNonNull(textCast(textRuns, expandIndex)).getValue();
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
        //TODO Implement this.
    }

    public void addBefore(Picture picture) {
        //TODO Implement this.
    }

    public void addAfter(String text) {
        //TODO Implement this.
    }

    public void addAfter(Picture picture) {
        //TODO Implement this.
    }

    public void replaceWith(String text) {
        //TODO Implement this.
    }

    public void replaceWith(Picture picture, WordprocessingMLPackage opcPackage) throws Exception {
        BinaryPartAbstractImage imagePart =
                BinaryPartAbstractImage.createImagePart(opcPackage, Picture.convertFileToByteArray(picture.getPicFile()));
        Inline inline = imagePart.createImageInline("Default",
                "Default", 1, 2, false);
        int index = opcPackage.getMainDocumentPart().getJaxbElement().getBody().getContent().indexOf(textRuns.get(0).getParent());
        opcPackage.getMainDocumentPart().getJaxbElement().getBody().getContent().set(index, Picture.addInlineImage(inline));
    }

    public void remove() {
        //TODO Implement this.
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

    private int nestedIndexValidation(R run) {
        int nestedWmlContentIndex = 0;
        for (int i = 0; i < run.getContent().size(); i++) {
            try {
                Text text = (Text) ((JAXBElement<?>) ((ArrayListWml<?>) run.getContent()).get(nestedWmlContentIndex)).getValue();
                if(!text.getValue().isEmpty())
                    return nestedWmlContentIndex;
            } catch (ClassCastException e) {
                nestedWmlContentIndex++;
            }
        }
        return nestedWmlContentIndex;
    }

    public Text textCast(List<R> textRuns, int index) {
        try {
            return (Text) ((JAXBElement<?>) ((ArrayListWml<?>) textRuns.get(index)
                    .getContent()).get(nestedIndexValidation(textRuns.get(index)))).getValue();
        } catch (IndexOutOfBoundsException | ClassCastException exc) {
            return null;
        }
    }

    public Text textCast(R run) {
        try {
            return (Text) ((JAXBElement<?>) ((ArrayListWml<?>) run
                    .getContent()).get(nestedIndexValidation(run))).getValue();
        } catch (IndexOutOfBoundsException | ClassCastException exc) {
            return null;
        }
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
