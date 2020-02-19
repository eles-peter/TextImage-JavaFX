package general;

import java.util.Objects;

public class FontChar {

    private String unicodeChar;
    private String fontFamily;

    public FontChar(String unicodeChar, String fontFamily) {
        this.unicodeChar = unicodeChar;
        this.fontFamily = fontFamily;
    }

    public String getUnicodeChar() {
        return unicodeChar;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    @Override
    public String toString() {
        return "FontChar{" +
                "unicodeCharacter='" + unicodeChar + '\'' +
                ", fontType='" + fontFamily + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FontChar fontChar = (FontChar) o;
        return Objects.equals(unicodeChar, fontChar.unicodeChar) &&
                Objects.equals(fontFamily, fontChar.fontFamily);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unicodeChar, fontFamily);
    }
}
