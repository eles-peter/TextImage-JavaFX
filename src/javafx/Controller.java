package javafx;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import impl.org.controlsfx.*;
import org.controlsfx.control.RangeSlider;

import general.*;

import java.io.File;
import java.io.IOException;

public class Controller {

    private final int MAXIMAGESIZE = 1000;

    private String actualFileName;
    private Luminosity sourceLuminosity;
    private Luminosity resizedLuminosity;
    private Luminosity modifiedLuminosity;
    private Modifier modifier = new Modifier();
    private int midTone;
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
    private TextField midToneText;
    @FXML
    private Rectangle thumb;
    @FXML
    private SplitPane p;
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
    private void moveThumb(MouseEvent action) {
        thumb.setTranslateX(action.getSceneX() - thumb.getWidth()/2);
        thumb.setTranslateY(action.getSceneY() - thumb.getHeight()/2);
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

    @FXML
    private void changeMidTone(ActionEvent action) {
        int newMidTone = Integer.parseInt(this.midToneText.getText());
        modifier.getValuesFrom(resizedLuminosity.getSortedItemMap());
        modifier.changeMidTone(newMidTone);
        modifiedLuminosity = resizedLuminosity.createModifiedLuminosity(modifier);
        actualizeImageAndView();
    }

    private void modifyImage() {

    }


    private void actualizeImageAndView() {
        newWidth.setText("" + modifiedLuminosity.getWidth());
        newHeight.setText("" + modifiedLuminosity.getHeight());
        midToneSlider.setValue(modifiedLuminosity.getMidTone());
        midToneValue.setText(modifiedLuminosity.getMidTone() + "%");

        WriteImage writeImage = new WriteImage(modifiedLuminosity.getLuminosityMap());
        imageView.setImage(writeImage.getWritableImage());
    }

    public void initialize() {




        midToneSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int newMidTone = newValue.intValue();
            modifier.getValuesFrom(resizedLuminosity.getSortedItemMap()); //TODO somewhere otherplace
            modifier.changeMidTone(newMidTone);
            modifiedLuminosity = resizedLuminosity.createModifiedLuminosity(modifier);
            modifiedLuminosity.setMidTone(newMidTone);
            actualizeImageAndView();
        });


    }


}
