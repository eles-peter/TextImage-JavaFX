package userInterface.utils;

import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

public class SingleSlider {

    private int sliderMin;
    private int sliderMax;
    private Rectangle sliderButton;
    private Rectangle sliderRail;
    private Label sliderLabel;
    private String labelTag;
    private int value;

    public SingleSlider(int sliderMin, int sliderMax, Rectangle sliderRail, Rectangle sliderButton, Label sliderLabel, String labelTag) {
        this.sliderMin = sliderMin;
        this.sliderMax = sliderMax;
        this.sliderButton = sliderButton;
        this.sliderRail = sliderRail;
        this.sliderLabel = sliderLabel;
        this.labelTag = labelTag;
    }

    public SingleSlider(int sliderMin, int sliderMax, Rectangle sliderRail, Rectangle sliderButton, Label sliderLabel) {
        this.sliderMin = sliderMin;
        this.sliderMax = sliderMax;
        this.sliderButton = sliderButton;
        this.sliderRail = sliderRail;
        this.sliderLabel = sliderLabel;
        this.labelTag = "";
    }

    public int setSliderValue(int newValue) {
        if (newValue > sliderMax) newValue = sliderMax;
        if (newValue < sliderMin) newValue = sliderMin;
        double sliderLength = sliderRail.getWidth() - sliderButton.getWidth();
        double sliderUnit = sliderLength / (sliderMax - sliderMin);
        sliderButton.setLayoutX((newValue  - sliderMin) * sliderUnit);
        if (sliderLabel != null) sliderLabel.setText(newValue + labelTag);
        return newValue;
    }

    public int clickOrDrag(MouseEvent action) {
        double sliderLength = sliderRail.getWidth() - sliderButton.getWidth();
        Bounds boundsInScene = sliderRail.localToScene(sliderRail.getBoundsInLocal());
        double sliderRailSceneX = boundsInScene.getMinX();
        double newPosition = action.getSceneX() - sliderRailSceneX - sliderButton.getWidth() / 2;
        if (newPosition < 0) newPosition = 0;
        if (newPosition > sliderLength) newPosition = sliderLength;
        sliderButton.setLayoutX(newPosition);

        double sliderUnit = sliderLength / (sliderMax - sliderMin);
        int newValue = (int) (newPosition / sliderUnit) + sliderMin;
        if (sliderLabel != null) sliderLabel.setText(newValue + labelTag);
        return newValue;
    }

}
