package eu.easyrpa.openframework.word;

import org.docx4j.model.fields.merge.DataFieldName;
import org.docx4j.model.fields.merge.MailMerger;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//TODO REMOVE THIS CLASS

public class DocVariables {
    private static final int SEED = 100_000;

    public void replaceVar(List<Object> texts, Map<String, String> values) {
        List<String> els = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int pass = 0;
        int prepare = 1;
        int read = 2;
        int mode = pass;
        List<int[]> toNullify = new ArrayList<>();
        int[] currentNullifyProps = new int[4];
        for (int i = 0; i < texts.size(); i++) {
            Object text = texts.get(i);
            Text textElement = (Text) text;
            String newVal = "";
            String sourceTextVal = textElement.getValue();
            StringBuilder sbText = new StringBuilder();
            int extra = 0;
            char[] vchars = sourceTextVal.toCharArray();
            for (int col = 0; col < vchars.length; col++) {
                char c = vchars[col];
                sbText.append(c);
                switch (c) {
                    case '$': {
                        mode = prepare;
                        sb.append(c);
                    }
                    break;
                    case '{': {
                        if (mode == prepare) {
                            sb.append(c);
                            mode = read;
                            currentNullifyProps[0] = i;
                            currentNullifyProps[1] = col + extra - 1;
                        } else {
                            if (mode == read) {
                                sb = new StringBuilder();
                                mode = pass;
                            }
                        }
                    }
                    break;
                    case '}': {
                        if (mode == read) {
                            mode = pass;
                            sb.append(c);
                            els.add(sb.toString());
                            newVal += sbText
                                    + (null == values.get(sb.toString()) ? sb.toString() : values.get(sb.toString()));
                            sbText = new StringBuilder();
                            currentNullifyProps[2] = i;
                            currentNullifyProps[3] = col + extra;
                            toNullify.add(currentNullifyProps);
                            currentNullifyProps = new int[4];
                            extra += sb.toString().length();
                            sb = new StringBuilder();
                        } else if (mode == prepare) {
                            mode = pass;
                            sb = new StringBuilder();
                        }
                    }
                    default: {
                        if (mode == read) sb.append(c);
                        else if (mode == prepare) {
                            mode = pass;
                            sb = new StringBuilder();
                        }
                    }
                }
            }
            newVal += sbText.toString();
            textElement.setValue(newVal);
        }
        // remove original expressions
        if (!toNullify.isEmpty())
            for (int i = 0; i < texts.size(); i++) {
                if (toNullify.isEmpty()) break;
                currentNullifyProps = toNullify.get(0);
                Object text = texts.get(i);
                Text textElement = (Text) text;
                String v = textElement.getValue();
                StringBuilder nvalSB = new StringBuilder();
                char[] textChars = v.toCharArray();
                for (int j = 0; j < textChars.length; j++) {
                    char c = textChars[j];
                    if (null == currentNullifyProps) {
                        nvalSB.append(c);
                        continue;
                    }
                    int floor = currentNullifyProps[0] * SEED + currentNullifyProps[1];
                    int ceil = currentNullifyProps[2] * SEED + currentNullifyProps[3];
                    int head = i * SEED + j;
                    if (!(head >= floor && head <= ceil)) {
                        nvalSB.append(c);
                    }
                    if (j > currentNullifyProps[3] && i >= currentNullifyProps[2]) {
                        toNullify.remove(0);
                        if (toNullify.isEmpty()) {
                            currentNullifyProps = null;
                            continue;
                        }
                        currentNullifyProps = toNullify.get(0);
                    }
                }
                textElement.setValue(nvalSB.toString());
            }
    }

    public void replaceByMailMerge(List<Map<DataFieldName, String>> replacedList, WordprocessingMLPackage mlPackage) throws Docx4JException {
        org.docx4j.model.fields.merge.MailMerger.setMERGEFIELDInOutput(MailMerger.OutputField.KEEP_MERGEFIELD);
        for (Map<DataFieldName, String> thismap : replacedList) {
            org.docx4j.model.fields.merge.MailMerger.performMerge(mlPackage, thismap, true);
        }
    }
}
