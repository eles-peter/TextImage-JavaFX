package UserInterface;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import general.*;
import javafx.stage.WindowEvent;


public class Controller {

    private final int MAXIMAGESIZE = 1000;
    private final int MIDTONESCALE = 100;

    private String actualFileName;
    private Luminosity sourceLuminosity;
    private Luminosity resizedLuminosity;
    private Luminosity modifiedLuminosity;
    private Modifier modifier = new Modifier();
    private int midTone = 50;
    private int minValue;
    private int maxValue;
    private boolean keepRatio;
    private boolean equalize;


    //<editor-fold defaultstate="collapsed" desc="FXML declarations">
    @FXML
    private SplitPane mainPane;
    @FXML
    private AnchorPane modifiersPane;
    @FXML
    private Label fileName;
    @FXML
    private ImageView imageView;
    @FXML
    private TextField newWidth;
    @FXML
    private TextField newHeight;
    @FXML
    private Group keepRatioButton;
    @FXML
    private Rectangle equalizeBackground;
    @FXML
    private Rectangle equalizeButton;
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
    //</editor-fold>

    @FXML
    private void openFile(ActionEvent action) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);

        ReadImageFile rgbimage = null;
        try {
            rgbimage = new ReadImageFile(selectedFile);
            //TODO alpha csatorna figyelembevétele???
            sourceLuminosity = new Luminosity(rgbimage.convertToLuminosityArray());
        } catch (IOException e) {
            alertMessage("Something went wrong, maybe try again!");
        } catch (Exception e) {
            alertMessage("These aren't droids you're looking for!");
        }

        rgbimage = null; // Törlés a memóriából....

        int sourceHeight = sourceLuminosity.getHeight();
        int sourceWidth = sourceLuminosity.getWidth();
        if (sourceHeight > sourceWidth && sourceHeight > MAXIMAGESIZE) {
            sourceLuminosity = sourceLuminosity.resizeToNewKeepRatioBaseHeight(MAXIMAGESIZE);
        } else if (sourceWidth > sourceHeight && sourceWidth > MAXIMAGESIZE) {
            sourceLuminosity = sourceLuminosity.resizeToNewKeepRatioBaseWidth(MAXIMAGESIZE);
        }

        resizedLuminosity = sourceLuminosity.clone();
        modifiedLuminosity = resizedLuminosity.clone();
        minValue = modifiedLuminosity.getSortedItemMap().firstKey();
        maxValue = modifiedLuminosity.getSortedItemMap().lastKey();
        modifiersPane.setDisable(false);
        modifiersPane.setOpacity(1);

        actualFileName = selectedFile.getName();
        fileName.setText("Actual file: " + actualFileName);

        setRangeSlide(minValue, maxValue);
        setMidToneSlide(50);
        setEqualizeFalse();

        actualizeImageAndView();
    }

//*******************KÉP ÁTMÉRETEZÉS************************************

    @FXML
    private void clickedKeepRatioButton(MouseEvent action) {
        if (keepRatio) {
            keepRatio = false;
            for (Node child : keepRatioButton.getChildren()) {
                ((Shape) child).setFill(Color.rgb(210, 210, 210));
            }
        } else {
            keepRatio = true;
            for (Node child : keepRatioButton.getChildren()) {
                ((Shape) child).setFill(Color.SKYBLUE);
            }
        }
    }

    @FXML
    private void resizeImageWidth(ActionEvent action) {
        try {
            int newWidth = Integer.parseInt(this.newWidth.getText());
            if (newWidth > MAXIMAGESIZE) newWidth = MAXIMAGESIZE;
            if (keepRatio) {
                resizedLuminosity = sourceLuminosity.resizeToNewKeepRatioBaseWidth(newWidth);
            } else {
                int newHeight = Integer.parseInt(this.newHeight.getText());
                resizedLuminosity = sourceLuminosity.resizeToNew(newWidth, newHeight);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        modifyImage();
        actualizeImageAndView();
    }

    @FXML
    private void resizeImageHeight(ActionEvent action) {
        try {
            int newHeight = Integer.parseInt(this.newHeight.getText());
            if (newHeight > MAXIMAGESIZE) newHeight = MAXIMAGESIZE;
            if (keepRatio) {
                resizedLuminosity = sourceLuminosity.resizeToNewKeepRatioBaseHeight(newHeight);
            } else {
                int newWidth = Integer.parseInt(this.newWidth.getText());
                resizedLuminosity = sourceLuminosity.resizeToNew(newWidth, newHeight);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        modifyImage();
        actualizeImageAndView();
    }

//************************EQUALIZE**************************************

    @FXML
    private void clickedEqualizeButton(MouseEvent action) {
        if (equalize) {
            equalize = false;
            equalizeButton.setLayoutX(0);
            equalizeBackground.setFill(Color.rgb(210, 210, 210));

        } else {
            equalize = true;
            equalizeButton.setLayoutX(equalizeBackground.getWidth() - equalizeButton.getWidth());
            equalizeBackground.setFill(Color.SKYBLUE);
            setRangeSlide(0, 255);
            setMidToneSlide(50);
        }
        modifyImage();
        actualizeImageAndView();
    }

    @FXML
    private void setEqualizeFalse() {
        equalize = false;
        equalizeButton.setLayoutX(0);
        equalizeBackground.setFill(Color.rgb(210, 210, 210));
    }

// ****************RANGE MŰVELETEK**************************

    //TODO write click on rail method!!!

    @FXML
    private void setRangeSlide(int newMinValue, int newMaxvalue) {
        if (newMaxvalue > 255) newMaxvalue = 255;
        if (newMinValue < 0) newMinValue = 0;
        double sliderLength = rangeSlideRail.getWidth() - rangeMinButton.getWidth() - rangeMaxButton.getWidth();
        double sliderUnit = sliderLength / 255;
        double newMinX = newMinValue * sliderUnit;
        rangeMinButton.setLayoutX(newMinX);
        rangeSlideRange.setLayoutX(newMinX + rangeMinButton.getWidth());
        rangeSlideRange.setFill(Color.SKYBLUE);
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
        double newMinX = action.getSceneX() - rangeSlideRail.getParent().getLayoutX() - rangeMinButton.getWidth() / 2;

        if (newMinX > rangeMaxButton.getLayoutX() - rangeMinButton.getWidth())
            newMinX = rangeMaxButton.getLayoutX() - rangeMinButton.getWidth();
        if (newMinX < 0) newMinX = 0;
        this.minValue = (int) (newMinX / sliderUnit);

        rangeMinButton.setLayoutX(newMinX);
        rangeSlideRange.setLayoutX(newMinX + rangeMinButton.getWidth());
        double newRangeLength = rangeMaxButton.getLayoutX() - newMinX - rangeMinButton.getWidth();
        rangeSlideRange.setWidth(newRangeLength);

        modifyImage();
        actualizeImageAndView();
    }

    @FXML
    private void dragRangeSlideMax(MouseEvent action) {
        double sliderLength = rangeSlideRail.getWidth() - rangeMinButton.getWidth() - rangeMaxButton.getWidth();
        double sliderUnit = sliderLength / 255;
        double newMaxX = action.getSceneX() - rangeSlideRail.getParent().getLayoutX() - rangeMinButton.getWidth() - rangeMaxButton.getWidth() / 2;

        if (newMaxX < rangeMinButton.getLayoutX()) newMaxX = rangeMinButton.getLayoutX();
        if (newMaxX > sliderLength) newMaxX = sliderLength;
        this.maxValue= (int) (newMaxX / sliderUnit);

        rangeMaxButton.setLayoutX(newMaxX + rangeMinButton.getWidth());
        double newRangeLength = newMaxX - rangeMinButton.getLayoutX();
        rangeSlideRange.setWidth(newRangeLength);

        modifyImage();
        actualizeImageAndView();
    }

//*********************MIDTONE MŰVELETEK************************************

    //TODO write click on rail method!!!

    @FXML
    private void setMidToneSlide(int newValue) {
        if (newValue > MIDTONESCALE) newValue = MIDTONESCALE;
        if (newValue < 0) newValue = 0;
        double sliderLength = midToneSlideRail.getWidth() - midToneSlideButton.getWidth();
        double sliderUnit = sliderLength / MIDTONESCALE;
        midToneSlideButton.setLayoutX(newValue * sliderUnit);
        this.midTone = newValue;
        midToneValue.setText(this.midTone + "%");
    }

    @FXML
    private void dragMidToneSlide(MouseEvent action) {
        double sliderLength = midToneSlideRail.getWidth() - midToneSlideButton.getWidth();
        double newPosition = action.getSceneX() - midToneSlideRail.getParent().getLayoutX() - midToneSlideButton.getWidth() / 2;
        if (newPosition < 0) newPosition = 0;
        if (newPosition > sliderLength) newPosition = sliderLength;
        midToneSlideButton.setLayoutX(newPosition);

        double sliderUnit = sliderLength / MIDTONESCALE;
        this.midTone = (int) (newPosition / sliderUnit);
        midToneValue.setText(this.midTone + "%");

        modifyImage();
        actualizeImageAndView();
    }

//********************MODIFY - VÉGREHAJTÁS, RESET******************

    private void modifyImage() {
        modifier.getValuesFrom(resizedLuminosity.getSortedItemMap());
        if (equalize) {
            modifier.equalize();
        }
        if (resizedLuminosity.getSortedItemMap().firstKey() != this.minValue ||
                resizedLuminosity.getSortedItemMap().lastKey() != this.maxValue) {
            modifier.changeRange(this.minValue, this.maxValue);
        }
        if (this.midTone != 50) {
            modifier.changeMidTone(this.midTone);
        }
        modifiedLuminosity = resizedLuminosity.createModifiedLuminosity(modifier);
    }

    @FXML
    private void resetModifiersButton(ActionEvent action) {
        resetModifiers();
    }


    private void resetModifiers() {
        setEqualizeFalse();
        setMidToneSlide(50);
        int resetedMinValue = resizedLuminosity.getSortedItemMap().firstKey();
        int resetedMaxValue = resizedLuminosity.getSortedItemMap().lastKey();
        setRangeSlide(resetedMinValue, resetedMaxValue);
        modifyImage();
        actualizeImageAndView();
    }

    private void actualizeImageAndView() {
        newWidth.setText("" + modifiedLuminosity.getWidth());
        newHeight.setText("" + modifiedLuminosity.getHeight());
        midToneValue.setText(midTone + "%");
        WriteImage writeImage = new WriteImage(modifiedLuminosity.getLuminosityMap());
        imageView.setImage(writeImage.getWritableImage());
    }

//********************ERROR POPUP WINDOW******************

    //TODO with dialogPane

    public void alertMessage(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ErrorMessage");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);
        alert.show();
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
