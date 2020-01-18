package javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
//import impl.org.controlsfx.*;
//import org.controlsfx.control.RangeSlider;

import general.*;

import java.io.File;
import java.io.IOException;

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
    private Slider midToneSlider;
    @FXML
    private Label midToneValue;
    @FXML
    private HBox singleSliderBox;
    @FXML
    private Rectangle singleSliderTrail0;
    @FXML
    private Rectangle singleSliderThumb;
    @FXML
    private Rectangle singleSliderTrail1;
    @FXML
    private TextField singleSlideInput;
    @FXML
    private Rectangle singleSlideRail;
    @FXML
    private Rectangle singleSlideButton;

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
        actualizeImageAndView();
    }


    @FXML
    private void modifyMidToneSlider(ActionEvent action) {
        setSingleSlide(Integer.parseInt(singleSlideInput.getText()), MIDTONESCALE);
    }

    @FXML
    private void moveThumb(MouseEvent action) {
        singleSliderThumb.setTranslateX(action.getSceneX() - singleSliderThumb.getWidth()/2);
    }

    @FXML
    private void setMidToneSlider(int newValue) {
        double sliderLength = singleSliderBox.getWidth() - singleSliderThumb.getWidth();
        double sliderUnit = sliderLength / MIDTONESCALE;
        double newTrail0Length = newValue * sliderUnit;
        if (newTrail0Length < singleSliderTrail0.getWidth()) {
            singleSliderTrail0.setWidth(newTrail0Length);
            singleSliderTrail1.setWidth(sliderLength-newTrail0Length);
        } else {
            singleSliderTrail1.setWidth(sliderLength-newTrail0Length);
            singleSliderTrail0.setWidth(newTrail0Length);
        }
    }



    @FXML
    private void dragSingleSlide(MouseEvent action) {
        double sliderLength = singleSlideRail.getWidth() - singleSlideButton.getWidth();
        double newPosition = action.getSceneX() - singleSlideRail.getParent().getLayoutX();
        if (newPosition < 0) newPosition = 0;
        if (newPosition > sliderLength) newPosition = sliderLength;
        singleSlideButton.setLayoutX(newPosition);

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
    private void setSingleSlide(int newValue, int range) {
        if (newValue > range) newValue = range;
        if (newValue < 0) newValue = 0;
        double sliderLength = singleSlideRail.getWidth() - singleSlideButton.getWidth();
        double sliderUnit = sliderLength / range;
        singleSlideButton.setLayoutX(newValue * sliderUnit);
    }







    @FXML
    private void resizeImageWidth(ActionEvent action) {
        try {
            int newWidth = Integer.parseInt(this.newWidth.getText());
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

    //TODO write reset method!!!


    private void modifyImage() {

    }


    private void actualizeImageAndView() {
        newWidth.setText("" + modifiedLuminosity.getWidth());
        newHeight.setText("" + modifiedLuminosity.getHeight());
        midToneSlider.setValue(midTone);
        midToneValue.setText(midTone + "%");
        setMidToneSlider(midTone);

        WriteImage writeImage = new WriteImage(modifiedLuminosity.getLuminosityMap());
        imageView.setImage(writeImage.getWritableImage());
    }

    public void initialize() {





        midToneSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            midTone = newValue.intValue();
            modifier.getValuesFrom(resizedLuminosity.getSortedItemMap()); //TODO somewhere otherplace
            modifier.changeMidTone(midTone);
            modifiedLuminosity = resizedLuminosity.createModifiedLuminosity(modifier);
            actualizeImageAndView();
        });


    }


}
