package eu.ibagroup.easyrpa.openframework.googlesheets.utils;

public class GSheetUtils {
    public static int[] convert(String coord) {
        int[] arr = new int[2];
        String column = "";
        int coordIdx = 0;
        while(Character.isLetter(coord.charAt(coordIdx++))){
            column += coord.charAt(coordIdx-1);
        }
        for (int  i = 0; i < column.length(); i++) {
            arr[0] *= 26;
            arr[0] += column.charAt(i) - 'A' + 1;
        }
        arr[0] -= 1;

        arr[1] = Integer.valueOf(coord.substring(coordIdx-1)) - 1;
        return arr;
    }

    public static String convertNumToColString(int col) {
        int excelColNum = col + 1;
        StringBuilder colRef = new StringBuilder(2);
        int colRemain = excelColNum;

        while(colRemain > 0) {
            int thisPart = colRemain % 26;
            if (thisPart == 0) {
                thisPart = 26;
            }

            colRemain = (colRemain - thisPart) / 26;
            char colChar = (char)(thisPart + 64);
            colRef.insert(0, colChar);
        }

        return colRef.toString();
    }

    public static String convertNumRangeToString(int[] topLeft, int[] bottomRight) {
        return convertNumCoordToString(topLeft)+":"+convertNumCoordToString(bottomRight);
    }

    public static String convertNumCoordToString(int[] coord) {
        return convertNumToColString(coord[0])+(coord[1]+1);
    }

    public static String convertNumCoordToString(int col, int row) {
        return convertNumToColString(col)+(row+1);
    }
}
