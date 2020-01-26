package userInterface.utils;

import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class RangeSlider {

    private int rangeSliderMin;
    private int rangeSliderMax;
    private Rectangle rangeSliderMinButton;
    private Rectangle rangeSliderMaxButton;
    private Rectangle rangeSliderRail;
    private Rectangle rangeSliderRange;
    private intRange rangeValue;

    public RangeSlider(int rangeSliderMin, int rangeSliderMax, Rectangle rangeSliderMinButton, Rectangle rangeSliderMaxButton, Rectangle rangeSliderRail, Rectangle rangeSliderRange) {
        this.rangeSliderMin = rangeSliderMin;
        this.rangeSliderMax = rangeSliderMax;
        this.rangeSliderMinButton = rangeSliderMinButton;
        this.rangeSliderMaxButton = rangeSliderMaxButton;
        this.rangeSliderRail = rangeSliderRail;
        this.rangeSliderRange = rangeSliderRange;
    }

    public intRange setRangeSlider(int newMinValue, int newMaxvalue) {
        intRange newRangeValue = new intRange(newMinValue, newMaxvalue);
        return setRangeSlider(newRangeValue);
    }

    public intRange setRangeSlider(intRange newRangeValue) {
        if (newRangeValue.max() > rangeSliderMax) newRangeValue.setMax(rangeSliderMax);
        if (newRangeValue.min() < rangeSliderMin) newRangeValue.setMin(rangeSliderMin);
        double sliderLength = rangeSliderRail.getWidth() - rangeSliderMinButton.getWidth() - rangeSliderMaxButton.getWidth();
        double sliderUnit = sliderLength / (rangeSliderMax - rangeSliderMin);
        double newMinX = (newRangeValue.min()  - rangeSliderMin) * sliderUnit;
        rangeSliderMinButton.setLayoutX(newMinX);
        rangeSliderRange.setLayoutX(newMinX + rangeSliderMinButton.getWidth());
        rangeSliderRange.setFill(Color.SKYBLUE);
        double newRangeLength = (newRangeValue.max() - newRangeValue.min()) * sliderUnit;
        rangeSliderRange.setWidth(newRangeLength);
        rangeSliderMaxButton.setLayoutX(newMinX + rangeSliderMinButton.getWidth() + newRangeLength);
        return this.rangeValue = newRangeValue;
    }

    public intRange dragRangeSlideMin(MouseEvent action) {
        double sliderLength = rangeSliderRail.getWidth() - rangeSliderMinButton.getWidth() - rangeSliderMaxButton.getWidth();
        double sliderUnit = sliderLength / 255;
        Bounds boundsInScene = rangeSliderRail.localToScene(rangeSliderRail.getBoundsInLocal());
        double rangeSliderRailSceneX = boundsInScene.getMinX();
        double newMinX = action.getSceneX() - rangeSliderRailSceneX - rangeSliderMinButton.getWidth() / 2;

        if (newMinX > rangeSliderMaxButton.getLayoutX() - rangeSliderMinButton.getWidth()) {
            newMinX = rangeSliderMaxButton.getLayoutX() - rangeSliderMinButton.getWidth();
        }
        if (newMinX < 0) newMinX = 0;
        int newMinValue = (int) (newMinX / sliderUnit) + rangeSliderMin;
        this.rangeValue.setMin(newMinValue);

        rangeSliderMinButton.setLayoutX(newMinX);
        rangeSliderRange.setLayoutX(newMinX + rangeSliderMinButton.getWidth());
        double newRangeLength = rangeSliderMaxButton.getLayoutX() - newMinX - rangeSliderMinButton.getWidth();
        rangeSliderRange.setWidth(newRangeLength);
        return rangeValue;
    }

    public intRange dragRangeSlideMax(MouseEvent action) {
        double sliderLength = rangeSliderRail.getWidth() - rangeSliderMinButton.getWidth() - rangeSliderMaxButton.getWidth();
        double sliderUnit = sliderLength / 255;
        Bounds boundsInScene = rangeSliderRail.localToScene(rangeSliderRail.getBoundsInLocal());
        double rangeSliderRailSceneX = boundsInScene.getMinX();
        double newMaxX = action.getSceneX() - rangeSliderRailSceneX - rangeSliderMinButton.getWidth() - rangeSliderMaxButton.getWidth() / 2;

        if (newMaxX < rangeSliderMinButton.getLayoutX()) newMaxX = rangeSliderMinButton.getLayoutX();
        if (newMaxX > sliderLength) newMaxX = sliderLength;
        int newMaxValue = (int) (newMaxX / sliderUnit) + rangeSliderMin;
        this.rangeValue.setMax(newMaxValue);

        rangeSliderMaxButton.setLayoutX(newMaxX + rangeSliderMinButton.getWidth());
        double newRangeLength = newMaxX - rangeSliderMinButton.getLayoutX();
        rangeSliderRange.setWidth(newRangeLength);
        return rangeValue;
    }

    public intRange clickOrDragRangeSlide(MouseEvent action) {
        Bounds boundsInScene = rangeSliderRail.localToScene(rangeSliderRail.getBoundsInLocal());
        double rangeSliderRailSceneX = boundsInScene.getMinX();
        double clickLayoutX = action.getSceneX() - rangeSliderRailSceneX;
        double minButtonCenterLayoutX = rangeSliderMinButton.getLayoutX() - rangeSliderMinButton.getWidth() / 2;
        double maxButtonCenterLayoutX = rangeSliderMaxButton.getLayoutX() - rangeSliderMaxButton.getWidth() / 2;
        double distanceFromMinButton = Math.abs(clickLayoutX - minButtonCenterLayoutX);
        double distanceFromMaxButton = Math.abs(clickLayoutX - maxButtonCenterLayoutX);
        intRange newRangeValue;
        if (distanceFromMaxButton < distanceFromMinButton) {
            newRangeValue = dragRangeSlideMax(action);
        } else {
            newRangeValue = dragRangeSlideMin(action);
        }
        return newRangeValue;
    }

}
