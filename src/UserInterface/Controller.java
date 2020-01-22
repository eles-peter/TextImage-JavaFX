package UserInterface;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

import general.*;
import UserInterface.utils.*;


public class Controller {

    private final int MAXIMAGESIZE = 1000;

    private String actualFileName;
    private Luminosity sourceLuminosity;
    private Luminosity resizedLuminosity;
    private Luminosity modifiedLuminosity;
    private Modifier modifier = new Modifier();
    private int midTone = 50;
    private int minValue;
    private int maxValue;
    private int offset = 0;
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
    private Rectangle rangeSliderRail;
    @FXML
    private Rectangle rangeMinButton;
    @FXML
    private Rectangle rangeMaxButton;
    @FXML
    private Rectangle rangeSliderRange;
    @FXML
    private Rectangle midToneSliderRail;
    @FXML
    private Rectangle midToneSliderButton;
    @FXML
    private Label midToneValue;
    @FXML
    private Rectangle offsetSliderRail;
    @FXML
    private Rectangle offsetSliderButton;
    @FXML
    private Label offsetValue;
    //</editor-fold>

    private SingleSlider midToneSlider;
    private SingleSlider offsetSlider;

    @FXML
    private void openFile(ActionEvent action) {
        File selectedFile = null;
        FileChooser fileChooser = new FileChooser();
//        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Image Files", AppConstants.SUPPORTED_IMAGE_EXTENSION);
//        fileChooser.getExtensionFilters().add(extensionFilter);

        //TODO filechooser file extension filter image
        selectedFile = fileChooser.showOpenDialog(null);

        ReadImageFile rgbimage = null;
        try {
            rgbimage = new ReadImageFile(selectedFile);
            //TODO alpha csatorna figyelembevétele???

            //TODO flichooser window cancel handling


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
        int newValue= midToneSlider.setSliderValue(50);
        //TODO nem kell ide offset beállítás? Az egészet helyettesíteni reset modifier-el....????
        System.out.println(newValue);
        this.midTone = newValue;
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
            this.midTone = midToneSlider.setSliderValue(50);
            this.offset = offsetSlider.setSliderValue(0);
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
        double sliderLength = rangeSliderRail.getWidth() - rangeMinButton.getWidth() - rangeMaxButton.getWidth();
        double sliderUnit = sliderLength / 255;
        double newMinX = newMinValue * sliderUnit;
        rangeMinButton.setLayoutX(newMinX);
        rangeSliderRange.setLayoutX(newMinX + rangeMinButton.getWidth());
        rangeSliderRange.setFill(Color.SKYBLUE);
        double newRangeLength = (newMaxvalue - newMinValue) * sliderUnit;
        rangeSliderRange.setWidth(newRangeLength);
        rangeMaxButton.setLayoutX(newMinX + rangeMinButton.getWidth() + newRangeLength);
        minValue = newMinValue;
        maxValue = newMaxvalue;
    }

    @FXML
    private void dragRangeSlideMin(MouseEvent action) {
        double sliderLength = rangeSliderRail.getWidth() - rangeMinButton.getWidth() - rangeMaxButton.getWidth();
        double sliderUnit = sliderLength / 255;
        double newMinX = action.getSceneX() - rangeSliderRail.getParent().getLayoutX() - rangeMinButton.getWidth() / 2;



        if (newMinX > rangeMaxButton.getLayoutX() - rangeMinButton.getWidth())
            newMinX = rangeMaxButton.getLayoutX() - rangeMinButton.getWidth();
        if (newMinX < 0) newMinX = 0;
        this.minValue = (int) (newMinX / sliderUnit);

        rangeMinButton.setLayoutX(newMinX);
        rangeSliderRange.setLayoutX(newMinX + rangeMinButton.getWidth());
        double newRangeLength = rangeMaxButton.getLayoutX() - newMinX - rangeMinButton.getWidth();
        rangeSliderRange.setWidth(newRangeLength);

        modifyImage();
        actualizeImageAndView();
    }

    @FXML
    private void dragRangeSlideMax(MouseEvent action) {
        double sliderLength = rangeSliderRail.getWidth() - rangeMinButton.getWidth() - rangeMaxButton.getWidth();
        double sliderUnit = sliderLength / 255;
        double newMaxX = action.getSceneX() - rangeSliderRail.getParent().getLayoutX() - rangeMinButton.getWidth() - rangeMaxButton.getWidth() / 2;

        if (newMaxX < rangeMinButton.getLayoutX()) newMaxX = rangeMinButton.getLayoutX();
        if (newMaxX > sliderLength) newMaxX = sliderLength;
        this.maxValue = (int) (newMaxX / sliderUnit);

        rangeMaxButton.setLayoutX(newMaxX + rangeMinButton.getWidth());
        double newRangeLength = newMaxX - rangeMinButton.getLayoutX();
        rangeSliderRange.setWidth(newRangeLength);

        modifyImage();
        actualizeImageAndView();
    }

    @FXML
    private void clickOrDragRangeSlide(MouseEvent action) {
        double clickLayoutX = action.getSceneX() - rangeSliderRail.getParent().getLayoutX();
        double minButtonCenterLayoutX = rangeMinButton.getLayoutX() - rangeMinButton.getWidth() / 2;
        double maxButtonCenterLayoutX = rangeMaxButton.getLayoutX() - rangeMaxButton.getWidth() / 2;
        double distanceFromMinButton = Math.abs(clickLayoutX - minButtonCenterLayoutX);
        double distanceFromMaxButton = Math.abs(clickLayoutX - maxButtonCenterLayoutX);
        if (distanceFromMaxButton < distanceFromMinButton) {
            dragRangeSlideMax(action);
        } else {
            dragRangeSlideMin(action);
        }
    }

//******************** MIDTONE MŰVELET ***********************************
    @FXML
    private void clickOrDragMidToneSlider(MouseEvent mouseEvent) {
        this.midTone = midToneSlider.clickOrDrag(mouseEvent);
        modifyImage();
        actualizeImageAndView();
    }

//******************** OFFSET MŰVELET ***********************************
    @FXML
    private void clickOrDragOffsetSlider(MouseEvent mouseEvent) {
        this.offset = offsetSlider.clickOrDrag(mouseEvent);
        //        TODO módosítani a range-t!
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
        if (this.offset != 0) {
            modifier.offset(this.offset);
            //TODO modositíni a ranget!!!
        }
        modifiedLuminosity = resizedLuminosity.createModifiedLuminosity(modifier);
    }

    @FXML
    private void resetModifiersButton(ActionEvent action) {
        resetModifiers();
    }


    private void resetModifiers() {
        setEqualizeFalse();
        this.midTone = midToneSlider.setSliderValue(50);
        this.offset = offsetSlider.setSliderValue(0);
        int resetedMinValue = resizedLuminosity.getSortedItemMap().firstKey();
        int resetedMaxValue = resizedLuminosity.getSortedItemMap().lastKey();
        setRangeSlide(resetedMinValue, resetedMaxValue);
        modifyImage(); // eTODO ez minek vam itt? // helyette lecserélni a modifiert, resizera...
        actualizeImageAndView();
    }

    private void actualizeImageAndView() {

        newWidth.setText("" + modifiedLuminosity.getWidth());
        newHeight.setText("" + modifiedLuminosity.getHeight());
        //TODO modositíni a ranget!!!
        WriteImage writeImage = new WriteImage(modifiedLuminosity.getLuminosityMap());
        imageView.setImage(writeImage.getWritableImage());
    }

//********************ERROR POPUP WINDOW******************


    public void alertMessage(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ErrorMessage");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);
        alert.show();
    }


    public void initialize() {
        midToneSlider = new SingleSlider(0, 100, midToneSliderRail, midToneSliderButton, midToneValue, "%");
        offsetSlider = new SingleSlider(-255, 255, offsetSliderRail, offsetSliderButton, offsetValue, "");


//        midToneSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
//            midTone = newValue.intValue();
//            modifier.getValuesFrom(resizedLuminosity.getSortedItemMap()); //TODO somewhere otherplace
//            modifier.changeMidTone(midTone);
//            modifiedLuminosity = resizedLuminosity.createModifiedLuminosity(modifier);
//            actualizeImageAndView();
//        });

    }

}
