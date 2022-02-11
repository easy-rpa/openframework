package eu.ibagroup.easyrpa.openframework.google.sheets.constants;

/**
 * Helps to identify the font family name for specific cells of Google Spreadsheet.
 */
public enum FontFamily {

    UNSPECIFIED(""),
    AMATIC_SC("Amatic SC"),
    ARIAL("Arial"),
    CAVEAT("Caveat"),
    COMFORTAA("Comfortaa"),
    COMIC_SANS_MS("Comic Sans MS"),
    COURIER_NEW("Courier New"),
    EB_GARAMOND("EB Garamond"),
    GEORGIA("Georgia"),
    IMPACT("Impact"),
    LOBSTER("Lobster"),
    LORA("Lora"),
    MERRIWEATHER("Merriweather"),
    MONTSERRAT("Montserrat"),
    NUNITO("Nunito"),
    OSWALD("Oswald"),
    PACIFICO("Pacifico"),
    PLAYFAIR_DISPLAY("Playfair Display"),
    ROBOTO("Roboto"),
    ROBOTO_MONO("Roboto Mono"),
    SPECTRAL("Spectral"),
    TIMES_NEW_ROMAN("Times New Roman"),
    TREBUCHET_MS("Trebuchet MS"),
    VERDANA("Verdana");

    /**
     * The name of font.
     */
    private String name;

    FontFamily(String name) {
    }

    /**
     * Gets font family name.
     *
     * @return string with font family name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets corresponding FontFamily by given font name.
     *
     * @param name the name of font.
     * @return FontFamily corresponding to given font name.
     */
    public static FontFamily getValue(String name) {
        for (FontFamily value : values()) {
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }
}
