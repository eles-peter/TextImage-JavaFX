package general;

import javafx.beans.property.SimpleStringProperty;

public class FontCharProperty {

    private SimpleStringProperty unicodeChar;
    private SimpleStringProperty fontFamily;

    public FontCharProperty(SimpleStringProperty unicodeChar, SimpleStringProperty fontFamily) {
        this.unicodeChar = unicodeChar;
        this.fontFamily = fontFamily;
    }

    public FontCharProperty(FontChar fontChar) {
        String unicodeChar = fontChar.getUnicodeChar();
        String fontFamily = fontChar.getFontFamily();
        this.unicodeChar = new SimpleStringProperty(unicodeChar);
        this.fontFamily = new SimpleStringProperty(fontFamily);
    }

    public void setFontChar(FontChar fontChar) {
        String unicodeChar = fontChar.getUnicodeChar();
        String fontFamily = fontChar.getFontFamily();
        this.unicodeChar.set(unicodeChar);
        this.fontFamily.set(fontFamily);
    }

    public String getUnicodeChar() {
        return unicodeChar.get();
    }

    public SimpleStringProperty unicodeCharProperty() {
        return unicodeChar;
    }

    public void setUnicodeChar(String unicodeChar) {
        this.unicodeChar.set(unicodeChar);
    }

    public String getFontFamily() {
        return fontFamily.get();
    }

    public SimpleStringProperty fontFamilyProperty() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily.set(fontFamily);
    }
}
