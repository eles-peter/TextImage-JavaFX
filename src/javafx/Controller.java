package javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
//import impl.org.controlsfx.*;
//import org.controlsfx.control.RangeSlider;

import java.io.File;
import java.io.IOException;

import general.*;

public class Controller {

    private final int MAXIMAGESIZE = 1000;
    private final int MIDTONESCALE = 100;

    private String actualFileName;
    private Luminosity sourceLuminosity;
    private Luminosity resizedLuminosity;
    private Luminosity modifiedLuminosity;
    private Modifier modifier = new Modifier();
    private int midTone = 50;
    private int minValue; //TODO kell ?
    private int maxValue; //TODO kell ?

    //<editor-fold defaultstate="collapsed" desc="FXML declarations">
        @FXML
    private Label fileName;
    @FXML
    private ImageView imageView;
    @FXML
    private TextField newWidth;
    @FXML
    private TextField newHeight;
    @FXML
    private RadioButton keepRatio;
    @FXML
    private Rectangle rangeSlideRail;
    @FXML
    private Rectangle rangeMinButton;
    @FXML
    private Rectangle rangeMaxButton;
    @FXML
    private Rectangle rangeSlideRange;
    @FXML
    private Rectangle midToneSlideRail;
    @FXML
    private Rectangle midToneSlideButton;
    @FXML
    private Label midToneValue;
    @FXML
    private Label rangeMinValue;
    @FXML
    private Label rangeMaxValue;
    //</editor-fold>

    @FXML
    private void openFile(ActionEvent action) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        actualFileName = selectedFile.getName();
        fileName.setText("Actual file: " + actualFileName);

        ReadImageFile RGBImage = null;
        try {
            RGBImage = new ReadImageFile(selectedFile);
        } catch (IOException e) {
            e.printStackTrace(); //TODO implement POPUP window with error message
            return;
        }
        sourceLuminosity = new Luminosity(RGBImage.convertToLuminosityArray());
        int sourceHeight = sourceLuminosity.getHeight();
        int sourceWidth = sourceLuminosity.getWidth();

        if (sourceHeight > sourceWidth && sourceHeight > MAXIMAGESIZE) {
            Luminosity tempLuminosity = sourceLuminosity.resizeToNewKeepRatioBaseHeight(MAXIMAGESIZE);
            sourceLuminosity = tempLuminosity;
        } else if (sourceWidth > sourceHeight && sourceWidth > MAXIMAGESIZE) {
            Luminosity tempLuminosity = sourceLuminosity.resizeToNewKeepRatioBaseWidth(MAXIMAGESIZE);
            sourceLuminosity = tempLuminosity;
        }

        resizedLuminosity = sourceLuminosity.clone();
        modifiedLuminosity = resizedLuminosity.clone();
        minValue = modifiedLuminosity.getSortedItemMap().firstKey();
        maxValue = modifiedLuminosity.getSortedItemMap().lastKey();
        setRangeSlide(minValue, maxValue);
        setMidToneSlide(50);

        actualizeImageAndView();
    }

    @FXML
    private void resizeImageWidth(ActionEvent action) {
        try {
            int newWidth = Integer.parseInt(this.newWidth.getText());
            if (newWidth > MAXIMAGESIZE) newWidth = MAXIMAGESIZE;
            if (keepRatio.isSelected()) {
                resizedLuminosity = sourceLuminosity.resizeToNewKeepRatioBaseWidth(newWidth);
            } else {
                int newHeight = Integer.parseInt(this.newHeight.getText());
                resizedLuminosity = sourceLuminosity.resizeToNew(newWidth, newHeight);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        modifiedLuminosity = resizedLuminosity.clone();
        actualizeImageAndView();
    }

    @FXML
    private void resizeImageHeight(ActionEvent action) {
        try {
            int newHeight = Integer.parseInt(this.newHeight.getText());
            if (newHeight > MAXIMAGESIZE) newHeight = MAXIMAGESIZE;
            if (keepRatio.isSelected()) {
                resizedLuminosity = sourceLuminosity.resizeToNewKeepRatioBaseHeight(newHeight);
            } else {
                int newWidth = Integer.parseInt(this.newWidth.getText());
                resizedLuminosity = sourceLuminosity.resizeToNew(newWidth, newHeight);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        modifiedLuminosity = resizedLuminosity.clone();
        actualizeImageAndView();
    }


    @FXML
    private void setRangeSlide(int newMinValue, int newMaxvalue) {
        if (newMaxvalue > 255) newMaxvalue = 255;
        if (newMinValue < 0) newMinValue = 0;
        double sliderLength = rangeSlideRail.getWidth() - rangeMinButton.getWidth() - rangeMaxButton.getWidth();
        double sliderUnit = sliderLength / 255;
        double newMinX = newMinValue * sliderUnit;
        rangeMinButton.setLayoutX(newMinX);
        rangeSlideRange.setLayoutX(newMinX + rangeMinButton.getWidth());
        double newRangeLength = (newMaxvalue - newMinValue) * sliderUnit;
        rangeSlideRange.setWidth(newRangeLength);
        rangeMaxButton.setLayoutX(newMinX + rangeMinButton.getWidth() + newRangeLength);
        minValue = newMinValue;
        maxValue = newMaxvalue;
    }

    @FXML
    private void dragRangeSlideMin(MouseEvent action) {
        double sliderLength = rangeSlideRail.getWidth() - rangeMinButton.getWidth() - rangeMaxButton.getWidth();
        double sliderUnit = sliderLength / 255;
        double newMinX = action.getSceneX() - rangeSlideRail.getParent().getLayoutX();

        if (newMinX > rangeMaxButton.getLayoutX() - rangeMinButton.getWidth()) newMinX = rangeMaxButton.getLayoutX() - rangeMinButton.getWidth();
        if (newMinX < 0) newMinX = 0;
        int newMinValue = (int) (newMinX / sliderUnit);
        this.minValue = newMinValue;

        rangeMinButton.setLayoutX(newMinX);
        rangeSlideRange.setLayoutX(newMinX + rangeMinButton.getWidth());
        double newRangeLength = rangeMaxButton.getLayoutX() - newMinX - rangeMinButton.getWidth();
        rangeSlideRange.setWidth(newRangeLength);

        rangeMinValue.setText("" + this.minValue);
    }

    @FXML
    private void dragRangeSlideMax(MouseEvent action) {
        double sliderLength = rangeSlideRail.getWidth() - rangeMinButton.getWidth() - rangeMaxButton.getWidth();
        double sliderUnit = sliderLength / 255;
        double newMaxX = action.getSceneX() - rangeSlideRail.getParent().getLayoutX() - rangeMinButton.getWidth();

        if (newMaxX < rangeMinButton.getLayoutX()) newMaxX = rangeMinButton.getLayoutX() ;
        if (newMaxX > sliderLength) newMaxX = sliderLength;
        int newMaxValue = (int) (newMaxX / sliderUnit);
        this.maxValue = newMaxValue;

        rangeMaxButton.setLayoutX(newMaxX + rangeMinButton.getWidth());
        double newRangeLength = newMaxX - rangeMinButton.getLayoutX();
        rangeSlideRange.setWidth(newRangeLength);

        rangeMaxValue.setText("" + maxValue);
    }





    @FXML
    private void dragMidToneSlide(MouseEvent action) {
        double sliderLength = midToneSlideRail.getWidth() - midToneSlideButton.getWidth();
        double newPosition = action.getSceneX() - midToneSlideRail.getParent().getLayoutX();
        if (newPosition < 0) newPosition = 0;
        if (newPosition > sliderLength) newPosition = sliderLength;
        midToneSlideButton.setLayoutX(newPosition);

        double sliderUnit = sliderLength / MIDTONESCALE;
        int newValue = (int) (newPosition / sliderUnit);
        midTone = newValue;
        midToneValue.setText(midTone + "%");

        modifier.getValuesFrom(resizedLuminosity.getSortedItemMap()); //TODO somewhere otherplace
        modifier.changeMidTone(midTone);
        modifiedLuminosity = resizedLuminosity.createModifiedLuminosity(modifier);
        actualizeImageAndView();
    }

    @FXML
    private void setMidToneSlide(int newValue) {
        if (newValue > MIDTONESCALE) newValue = MIDTONESCALE;
        if (newValue < 0) newValue = 0;
        double sliderLength = midToneSlideRail.getWidth() - midToneSlideButton.getWidth();
        double sliderUnit = sliderLength / MIDTONESCALE;
        midToneSlideButton.setLayoutX(newValue * sliderUnit);
        midTone = newValue;
        midToneValue.setText(midTone + "%");
    }







    //TODO write reset method!!!


    private void modifyImage() {

    }


    private void actualizeImageAndView() {
        newWidth.setText("" + modifiedLuminosity.getWidth());
        newHeight.setText("" + modifiedLuminosity.getHeight());

        midToneValue.setText(midTone + "%");
        WriteImage writeImage = new WriteImage(modifiedLuminosity.getLuminosityMap());
        imageView.setImage(writeImage.getWritableImage());
    }

    public void initialize() {

//        midToneSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
//            midTone = newValue.intValue();
//            modifier.getValuesFrom(resizedLuminosity.getSortedItemMap()); //TODO somewhere otherplace
//            modifier.changeMidTone(midTone);
//            modifiedLuminosity = resizedLuminosity.createModifiedLuminosity(modifier);
//            actualizeImageAndView();
//        });

    }

}
