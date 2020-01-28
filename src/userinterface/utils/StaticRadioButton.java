package userinterface.utils;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Shape;
import javafx.scene.paint.Color;

public class StaticRadioButton {

    private Group groupOfButtonElement;
    private boolean value;

    public StaticRadioButton(Group groupOfButtonElement) {
        this.groupOfButtonElement = groupOfButtonElement;
    }

    public boolean setRadioButtonFalse() {
        this.value = false;
        for (Node child : this.groupOfButtonElement.getChildren()) {
            ((Shape) child).setFill(Color.rgb(210, 210, 210));
        }
        return this.value;
    }

    public boolean setRadioButtonTrue() {
        this.value = true;
        for (Node child : this.groupOfButtonElement.getChildren()) {
            ((Shape) child).setFill(Color.SKYBLUE);
        }
        return this.value;
    }

    public boolean setRadioButton(boolean newValue) {
        if (newValue == true) setRadioButtonTrue();
        else setRadioButtonFalse();
        return this.value;
    }

    public boolean changeRadioButton() {
        setRadioButton(!this.value);
        return this.value;
    }

    public boolean isValue() {
        return value;
    }
}
