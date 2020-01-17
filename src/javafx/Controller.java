package javafx;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import general.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

public class Controller {

    private final int MAXIMAGESIZE = 1000;

    private String actualFileName;
    private Luminosity sourceLuminosity;
    private Luminosity resizedLuminosity;
    private Luminosity modifiedLuminosity;
    private Modifier modifier = new Modifier();

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
    private Slider midTone;
    @FXML
    private Label midToneValue;
    @FXML
    private TextField midToneText;


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
        actualizeImage();
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
        actualizeImage();
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
        actualizeImage();
    }

    //TODO write reset method!!!

    @FXML
    private void changeMidTone(ActionEvent action) {
        int newMidTone = Integer.parseInt(this.midToneText.getText());
        modifier.getValuesFrom(resizedLuminosity.getSortedItemMap());
        modifier.changeMidTone(newMidTone);
        modifiedLuminosity = resizedLuminosity.createModifiedLuminosity(modifier);
        actualizeImage();
    }


    private void actualizeImage() {
        newWidth.setText("" + modifiedLuminosity.getWidth());
        newHeight.setText("" + modifiedLuminosity.getHeight());
        midTone.setValue(modifiedLuminosity.getMidTone());
        midToneValue.setText(modifiedLuminosity.getMidTone() + "%");

        WriteImage writeImage = new WriteImage(modifiedLuminosity.getLuminosityMap());
        imageView.setImage(writeImage.getWritableImage());
    }

    public void initialize() {
        midTone.valueProperty().addListener((observable, oldValue, newValue) -> {
            int newMidTone = newValue.intValue();
            modifier.getValuesFrom(resizedLuminosity.getSortedItemMap()); //TODO somewhere otherplace
            modifier.changeMidTone(newMidTone);
            modifiedLuminosity = resizedLuminosity.createModifiedLuminosity(modifier);
            modifiedLuminosity.setMidTone(newMidTone);
            actualizeImage();
        });


    }

}
