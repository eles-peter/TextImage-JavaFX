package userinterface.utils;

import general.FontChar;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.Objects;

public class CharPane extends StackPane {

    private static final int FONTSIZE = 15;
    private static final int FONTPANELSIZE = 30;

    private Text text = new Text();
    private FontChar fontChar;
    private boolean isSelected;

    public CharPane(String unicodeChar, String fontFamily) {
        this(new FontChar(unicodeChar, fontFamily));
    }

    public CharPane(FontChar fontChar) {
        this.fontChar = fontChar;
        this.text.setFont(Font.font(fontChar.getFontFamily(), FONTSIZE));
        this.text.setText(fontChar.getUnicodeChar());
        this.getChildren().add(text);
        this.setPrefSize(FONTPANELSIZE, FONTPANELSIZE);
        this.setStyle("-fx-background-color: #FFFFFF;");
        this.setMouseEvents();
    }

    private void setMouseEvents() {
        this.setOnMouseClicked((event) -> {
            if (event.isShiftDown()) {
                this.multiselect();
            } else {
                this.select();
            }
        });

    }

    public void setIsSelectTrue() {
        this.isSelected = true;
        this.setStyle("-fx-background-color: #00BFFF;");
        this.text.setStyle("-fx-fill: #FFFFFF;");
    }

    public void setIsSelectFalse() {
        this.isSelected = false;
        this.setStyle("-fx-background-color: #FFFFFF;");
        this.text.setStyle("-fx-fill: #000000;");
    }

    public void select() {
        if (isSelected) {
            setIsSelectFalse();
        } else {
            setIsSelectTrue();
        }
    }

    private void multiselect() {
//        this.getParent().g
    }

    public String getUnicodeChar() {
        return this.fontChar.getUnicodeChar();
    }

    public String getFontFamily() {
        return this.fontChar.getFontFamily();
    }

    public FontChar getFontChar() {
        return this.fontChar;
    }

    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharPane charPane = (CharPane) o;
        return Objects.equals(fontChar, charPane.fontChar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fontChar);
    }
}
