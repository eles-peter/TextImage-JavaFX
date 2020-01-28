package userinterface.utils;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class RadioButton {

    private Rectangle radioButtonBackground;
    private Rectangle radioButtonButton;
    private boolean value;

    public RadioButton(Rectangle radioButtonBackground, Rectangle radioButtonButton) {
        this.radioButtonBackground = radioButtonBackground;
        this.radioButtonButton = radioButtonButton;
        this.value = false;
    }

    public boolean setRadioButton(boolean newValue) {
        if (newValue) setTrue();
        else setFalse();
        return newValue;
    }

    public boolean changeRadioButton(boolean baseValue) {
        if (baseValue) setFalse();
        else setTrue();
        return !baseValue;
    }

    private void setTrue() {
        value = true;
        radioButtonButton.setLayoutX(radioButtonBackground.getWidth() - radioButtonButton.getWidth());
        radioButtonBackground.setFill(Color.SKYBLUE);
    }

    private void setFalse() {
        value = false;
        radioButtonButton.setLayoutX(0);
        radioButtonBackground.setFill(Color.rgb(210, 210, 210));
    }

    public boolean isValue() {
        return value;
    }
}
