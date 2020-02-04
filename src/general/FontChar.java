package general;

import java.util.Objects;

public class FontChar {

    private String unicodeValue; //TODO átnevezni a FontChar osztályban a változó nevet
    private String fontType;

    public FontChar(String unicodeValue, String fontType) {
        this.unicodeValue = unicodeValue;
        this.fontType = fontType;
    }

    public String getUnicodeValue() {
        return unicodeValue;
    }

    public String getFontType() {
        return fontType;
    }

    @Override
    public String toString() {
        return "FontChar{" +
                "unicodeValue='" + unicodeValue + '\'' +
                ", fontType='" + fontType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FontChar fontChar = (FontChar) o;
        return unicodeValue.equals(fontChar.unicodeValue) &&
                fontType.equals(fontChar.fontType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unicodeValue, fontType);
    }
}
