package userInterface;

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
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import general.*;
import userInterface.utils.*;


public class Controller {

    private final int MAXIMAGESIZE = 1000;

    private String actualFileName;
    private Luminosity sourceLuminosity;
    private Luminosity resizedLuminosity;
    private Luminosity modifiedLuminosity;
    private Modifier modifier = new Modifier();
    private int midTone = 50;
    private intRange range = new intRange(0,255);
    private int offset = 0;
    private boolean keepRatio;
    private boolean equalize;

    //<editor-fold defaultstate="collapsed" desc="FXML declarations">

    private Stage primaryStage;
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
    private RangeSlider rangeSlider;

    @FXML
    private void openFile(ActionEvent action) {
        File selectedFile = null;
        ReadImageFile rgbimage = new ReadImageFile();
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        primaryStage = (Stage) mainPane.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile == null) return;

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
        actualFileName = selectedFile.getName();
        fileName.setText("Actual file: " + actualFileName);


        //TODO kiírás initializáló methodusba....

        int sourceHeight = sourceLuminosity.getHeight();
        int sourceWidth = sourceLuminosity.getWidth();
        if (sourceHeight > sourceWidth && sourceHeight > MAXIMAGESIZE) {
            sourceLuminosity = sourceLuminosity.resizeToNewKeepRatioBaseHeight(MAXIMAGESIZE);
        } else if (sourceWidth > sourceHeight && sourceWidth > MAXIMAGESIZE) {
            sourceLuminosity = sourceLuminosity.resizeToNewKeepRatioBaseWidth(MAXIMAGESIZE);
        }

        resizedLuminosity = sourceLuminosity.clone();
        modifiedLuminosity = resizedLuminosity.clone();

        modifiersPane.setDisable(false);
        modifiersPane.setOpacity(1);

        setSizeLabelsToActualValue();
        setModifiersToInitialValue();
        setRangeToActualValue();
        actualizeImageAndView();
    }

    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        ReadImageFile rgbimage = new ReadImageFile();
        List<String> formatNames = new ArrayList<>();
        for (String formatName : rgbimage.getFormatNames()) {
            formatNames.add("*." + formatName);
        }
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Images", formatNames));
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
        setSizeLabelsToActualValue();
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
        setSizeLabelsToActualValue();
        modifyImage();
        actualizeImageAndView();
    }

//************************EQUALIZE**************************************
    @FXML
    private void clickedEqualizeButton(MouseEvent action) {
        if (equalize) {
            setEqualizeFalse();
        } else {
            equalize = true;
            equalizeButton.setLayoutX(equalizeBackground.getWidth() - equalizeButton.getWidth());
            equalizeBackground.setFill(Color.SKYBLUE);
            range = rangeSlider.setRangeSlider(0, 255);
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
    @FXML
    private void dragRangeSliderMin(MouseEvent action) {
        this.range = rangeSlider.dragRangeSlideMin(action);
        modifyImage();
        actualizeImageAndView();
    }

    @FXML
    private void dragRangeSliderMax(MouseEvent action) {
        this.range = rangeSlider.dragRangeSlideMax(action);
        modifyImage();
        actualizeImageAndView();
    }

    @FXML
    private void clickOrDragRangeSlider(MouseEvent action) {
        this.range = rangeSlider.clickOrDragRangeSlide(action);
        modifyImage();
        actualizeImageAndView();
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
        int newMinValue = resizedLuminosity.getSortedItemMap().firstKey() + offset;
//        if (newMinValue < 0) newMinValue = 0;
//        int newMaxValue = resizedLuminosity.getSortedItemMap().lastKey() + offset;
//        if (newMaxValue > 255) newMaxValue = 255;
//        range.set(newMinValue, newMaxValue);
        modifyImage();
        actualizeImageAndView();
    }

//********************MODIFY - VÉGREHAJTÁS, RESET******************

    private void modifyImage() {
        modifier.getValuesFrom(resizedLuminosity.getSortedItemMap());
        if (equalize) {
            modifier.equalize();
        }
        if (this.offset != 0) {
            modifier.offset(this.offset);
        }
        if (modifier.getFirstValue() != this.range.min() ||
                modifier.getLastValue() != this.range.max()) {
            modifier.changeRange(this.range.min(), this.range.max());
        }
        if (this.midTone != 50) {
            modifier.changeMidTone(this.midTone);
        }
        modifiedLuminosity = resizedLuminosity.createModifiedLuminosity(modifier);
        setRangeToActualValue();
//        rangeSlider.setRangeSlider(modifier.getFirstValue(), modifier.getLastValue()); //TODO nem tudom mi a fasz baj van....
    }

    @FXML
    private void resetModifiers() {
        modifiedLuminosity = resizedLuminosity.clone();
        setModifiersToInitialValue();
        setRangeToActualValue();
        actualizeImageAndView();
    }

    private void setSizeLabelsToActualValue() {
        newWidth.setText("" + resizedLuminosity.getWidth());
        newHeight.setText("" + resizedLuminosity.getHeight());
    }

    private void setModifiersToInitialValue() {
        setEqualizeFalse();
        this.midTone = midToneSlider.setSliderValue(50);
        this.offset = offsetSlider.setSliderValue(0);
        actualizeImageAndView();
    }

    private void setRangeToActualValue() {
        int newMinValue = modifiedLuminosity.getSortedItemMap().firstKey();
        int newMaxValue = modifiedLuminosity.getSortedItemMap().lastKey();
        this.range.set(newMinValue, newMaxValue);
        rangeSlider.setRangeSlider(newMinValue, newMaxValue); // TODO lehet, hogy itt a baj....
    }

    private void actualizeImageAndView() {
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
//******************** INICIALIZÁLÁS ***********************************

    public void initialize() {
        midToneSlider = new SingleSlider(0, 100, midToneSliderRail, midToneSliderButton, midToneValue, "%");
        offsetSlider = new SingleSlider(-255, 255, offsetSliderRail, offsetSliderButton, offsetValue, "");
        rangeSlider = new RangeSlider(0,255, rangeMinButton, rangeMaxButton, rangeSliderRail, rangeSliderRange);



//        midToneSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
//            midTone = newValue.intValue();
//            modifier.getValuesFrom(resizedLuminosity.getSortedItemMap()); //TODO somewhere otherplace
//            modifier.changeMidTone(midTone);
//            modifiedLuminosity = resizedLuminosity.createModifiedLuminosity(modifier);
//            actualizeImageAndView();
//        });

    }

}
