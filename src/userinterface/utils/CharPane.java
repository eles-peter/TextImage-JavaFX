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
        double textWidth = this.text.getBoundsInLocal().getWidth();
        this.setPrefSize(FONTPANELSIZE, FONTPANELSIZE);
        if (textWidth > 0.95 * FONTPANELSIZE) {
            this.setPrefWidth(2 * FONTPANELSIZE + 1);
        }
        this.setStyle("-fx-background-color: #FFFFFF;");
        this.setMouseEvents();
        this.getChildren().add(text);
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
            ((SelectableFlowPane) this.getParent()).setLastSelected(this);
        } else {
            setIsSelectTrue();
            ((SelectableFlowPane) this.getParent()).setLastSelected(this);
        }
    }

    private void multiselect() {
        SelectableFlowPane parentFlowPane = (SelectableFlowPane) this.getParent();
        if (parentFlowPane.isLastSelected()) {
            CharPane lastSelected = parentFlowPane.getLastSelected();
            int indexA = parentFlowPane.getChildren().indexOf(this);
            int indexB = parentFlowPane.getChildren().indexOf(lastSelected);
            int start = Math.min(indexA, indexB + 1);
            int end = Math.max(indexA, indexB - 1);
            for (int i = start; i <= end; i++) {
                CharPane actualCharPane = (CharPane) parentFlowPane.getChildren().get(i);
                actualCharPane.select();
            }
        } else {
            select();
        }
    }

    public CharPane copy() {
        return new CharPane(getUnicodeChar(), getFontFamily());
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

    public Text getText() {
        return text;
    }

    public FontChar copyFontChar() {
        return new FontChar(getUnicodeChar(), getFontFamily());
    }

    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharPane charPane = (CharPane) o;
        return fontChar.equals(charPane.fontChar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fontChar);
    }
}
