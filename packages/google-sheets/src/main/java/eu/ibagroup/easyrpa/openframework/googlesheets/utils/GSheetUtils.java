package eu.ibagroup.easyrpa.openframework.googlesheets.utils;

import com.google.api.services.sheets.v4.model.GridRange;

import java.util.Objects;

public class GSheetUtils {
    public static int[] convert(String coord) {
        int[] arr = new int[2];
        String column = "";
        int coordIdx = 0;
        while (Character.isLetter(coord.charAt(coordIdx++))) {
            column += coord.charAt(coordIdx - 1);
        }
        for (int i = 0; i < column.length(); i++) {
            arr[0] *= 26;
            arr[0] += column.charAt(i) - 'A' + 1;
        }
        arr[0] -= 1;

        arr[1] = Integer.parseInt(coord.substring(coordIdx - 1)) - 1;
        return arr;
    }

    public static String convertNumToColString(int col) {
        int excelColNum = col + 1;
        StringBuilder colRef = new StringBuilder(2);
        int colRemain = excelColNum;

        while (colRemain > 0) {
            int thisPart = colRemain % 26;
            if (thisPart == 0) {
                thisPart = 26;
            }

            colRemain = (colRemain - thisPart) / 26;
            char colChar = (char) (thisPart + 64);
            colRef.insert(0, colChar);
        }

        return colRef.toString();
    }

    public static String convertNumRangeToString(int[] topLeft, int[] bottomRight) {
        return convertNumCoordToString(topLeft) + ":" + convertNumCoordToString(bottomRight);
    }

    public static String convertNumCoordToString(int[] coord) {
        return convertNumToColString(coord[0]) + (coord[1] + 1);
    }

    public static String convertNumCoordToString(int col, int row) {
        return convertNumToColString(col) + (row + 1);
    }

    public static boolean isOneRangeIsPartOfAnother(GridRange range1, GridRange range2) {
        if (range1.getSheetId().equals(range2.getSheetId())) {
            return range1.getStartRowIndex() <= range2.getEndRowIndex() &&
                    range1.getStartColumnIndex() <= range2.getEndColumnIndex() &&
                    range2.getStartRowIndex() <= range1.getEndRowIndex() &&
                    range2.getStartColumnIndex() <= range1.getEndColumnIndex();
        }
        return false;
    }

    public static boolean isTheSameCellRange(GridRange range1, GridRange range2) {
        if (range1.getSheetId().equals(range2.getSheetId())) {
            return Objects.equals(range1.getStartRowIndex(), range2.getStartRowIndex()) &&
                    Objects.equals(range1.getStartColumnIndex(), range2.getStartColumnIndex()) &&
                    Objects.equals(range2.getEndRowIndex(), range1.getEndRowIndex()) &&
                    Objects.equals(range2.getEndColumnIndex(), range1.getEndColumnIndex());
        }
        return false;
    }

    public static boolean isOneRangeNextToAnother(GridRange range1, GridRange range2) {
        if (range1.getSheetId().equals(range2.getSheetId())) {
            if (Objects.equals(range1.getStartColumnIndex(), range2.getStartColumnIndex()) &&
                    Objects.equals(range1.getEndColumnIndex(), range2.getEndColumnIndex())) {
                return Objects.equals(range1.getStartRowIndex(), range2.getEndRowIndex()) ||
                        Objects.equals(range1.getEndRowIndex(), range2.getStartRowIndex());
            }
            if (Objects.equals(range1.getStartRowIndex(), range2.getStartRowIndex()) &&
                    Objects.equals(range1.getEndRowIndex(), range2.getEndRowIndex())) {
                return Objects.equals(range1.getStartColumnIndex(), range2.getEndColumnIndex()) ||
                        Objects.equals(range1.getEndColumnIndex(), range2.getStartColumnIndex());
            }
        }
        return false;
    }
}
