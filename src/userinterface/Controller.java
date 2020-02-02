package userinterface;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.*;

import general.*;
import userinterface.utils.*;
import userinterface.utils.RadioButton;


public class Controller {

    private static final int MAXIMAGESIZE = 1000;
    private static final int UNDOLISTMAXSIZE = 20;

    private String actualFileName;
    private LumMap sourceLumMap;
    private LumMap resizedLumMap;
    private LumMap modifiedLumMap;
    private boolean keepRatio;
    private boolean equalize;
    private int midTone = 50;
    private IntRange range = new IntRange(0, 255);
    private int offset = 0;
    private Deque<ModiferValues> undoList = new LinkedList<>();

    private class ModiferValues {
        private boolean equalize;
        private int midTone;
        private IntRange range;
        private int offset;

        ModiferValues(boolean equalize, int midTone, IntRange range, int offset) {
            this.equalize = equalize;
            this.midTone = midTone;
            this.range = range;
            this.offset = offset;
        }

        boolean isEqualize() {
            return equalize;
        }

        int getMidTone() {
            return midTone;
        }

        IntRange getRange() {
            return range;
        }

        int getOffset() {
            return offset;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ModiferValues that = (ModiferValues) o;
            return equalize == that.equalize &&
                    midTone == that.midTone &&
                    offset == that.offset &&
                    range.equals(that.range);
        }

        @Override
        public int hashCode() {
            return Objects.hash(equalize, midTone, range, offset);
        }

        @Override
        public String toString() {
            return "{" +
                    "equalize=" + equalize +
                    ", midTone=" + midTone +
                    ", range=" + range +
                    ", offset=" + offset +
                    '}';
        }
    }

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
    private Group keepRatioButtonGroup;
    @FXML
    private Button resetSizeButton;
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
    @FXML
    private Button resetModifiersButton;
    @FXML
    private Button undoButton;
    //</editor-fold>

    private StaticRadioButton keepRatioButton;
    private RadioButton equalizeRadioButton;
    private SingleSlider midToneSlider;
    private SingleSlider offsetSlider;
    private RangeSlider rangeSlider;

    @FXML
    private void openFile() {
        File selectedFile = null;
        ReadImageFile rgbimage ;
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        primaryStage = (Stage) mainPane.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile == null) return;

        try {
            rgbimage = new ReadImageFile(selectedFile);
            //TODO alpha csatorna figyelembevétele???
            sourceLumMap = rgbimage.convertToLumMap();
        } catch (IOException e) {
            alertMessage("Something went wrong, maybe try again!");
        } catch (Exception e) {
            alertMessage("These aren't droids you're looking for!");
        }
        rgbimage = null; // Törlés a memóriából....
        actualFileName = selectedFile.getName();
        fileName.setText("Actual file: " + actualFileName);

        sourceLumMap = sourceLumMap.resizeToNewMaxSize(MAXIMAGESIZE);
        resizedLumMap = sourceLumMap.clone();
        modifiedLumMap = resizedLumMap.clone();

        modifiersPane.setDisable(false);
        modifiersPane.setOpacity(1);
        resetSizeButton.setDisable(true);
        undoButton.setDisable(true);
        resetModifiersButton.setDisable(true);

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
    private void clickedKeepRatioButton() {
        this.keepRatio = keepRatioButton.changeRadioButton();
    }

    @FXML
    private void resizeImageWidth() {
        try {
            int actualNewWidth = Integer.parseInt(this.newWidth.getText());
            int actualHeight = Integer.parseInt(this.newHeight.getText());
            double baseRatio = (double) resizedLumMap.getWidth() / resizedLumMap.getHeight();
            try {
                if (keepRatio) resizedLumMap = sourceLumMap.resizeToNewKeepRatioBaseWidth(actualNewWidth, baseRatio, MAXIMAGESIZE);
                else resizedLumMap = sourceLumMap.resizeToNew(actualNewWidth, actualHeight, MAXIMAGESIZE);
                resetSizeButton.setDisable(false);
            } catch (IllegalArgumentException e) {
                alertMessage("The maximum values of the rows and columns are " + MAXIMAGESIZE + "!");
            }
        } catch (NumberFormatException e) {
            alertMessage("Please gimme numbers!");
        }
        modifiedLumMap = resizedLumMap.clone();
        setSizeLabelsToActualValue();
        modifyImageAndView();
    }

    @FXML
    private void resizeImageHeight() {
        try {
            int actualNewHeight = Integer.parseInt(this.newHeight.getText());
            int actualNewWidth = Integer.parseInt(this.newWidth.getText());
            double baseRatio = (double) resizedLumMap.getWidth() / resizedLumMap.getHeight();
            try {
                if (keepRatio) resizedLumMap = sourceLumMap.resizeToNewKeepRatioBaseHeight(actualNewHeight, baseRatio, MAXIMAGESIZE);
                else resizedLumMap = sourceLumMap.resizeToNew(actualNewWidth, actualNewHeight, MAXIMAGESIZE);
                resetSizeButton.setDisable(false);
            } catch (IllegalArgumentException e) {
                alertMessage("The maximum values of the rows and columns are " + MAXIMAGESIZE + "!");
            }
        } catch (NumberFormatException e) {
            alertMessage("Please gimme numbers!");
        }
        modifiedLumMap = resizedLumMap.clone();
        setSizeLabelsToActualValue();
        modifyImageAndView();
    }

    @FXML
    private void resetImageSize() {
        resizedLumMap = sourceLumMap.clone();
        modifiedLumMap = resizedLumMap.clone();
        resetSizeButton.setDisable(true);
        setSizeLabelsToActualValue();
        modifyImageAndView();
    }

    //************************EQUALIZE**************************************
    @FXML
    private void clickedEqualizeButton() {
        addActionToUndoList();
        this.equalize = equalizeRadioButton.changeRadioButton(this.equalize);
        if (equalize) {
            range = rangeSlider.setRangeSlider(0, 255);
            this.midTone = midToneSlider.setSliderValue(50);
            this.offset = offsetSlider.setSliderValue(0);
        }
        modifyImageAndView();
    }

    // ****************RANGE MŰVELETEK**************************
    @FXML
    private void dragRangeSliderMin(MouseEvent action) {
        this.range = rangeSlider.dragRangeSlideMin(action);
        modifyImageAndView();
    }

    @FXML
    private void dragRangeSliderMax(MouseEvent action) {
        this.range = rangeSlider.dragRangeSlideMax(action);
        modifyImageAndView();
    }

    @FXML
    private void clickOnRangeButtons() {
        addActionToUndoList();
    }

    @FXML
    private void clickOnRangeSlider(MouseEvent action) {
        addActionToUndoList();
        this.range = rangeSlider.clickOrDragRangeSlide(action);
        modifyImageAndView();
    }

    //******************** MIDTONE MŰVELET ***********************************
    @FXML
    private void dragMidToneButton(MouseEvent mouseEvent) {
//        if (mouseEvent.isDragDetect()) addActionToUndoList();
        this.midTone = midToneSlider.clickOrDrag(mouseEvent);
        modifyImageAndView();
    }

    @FXML
    private void pressOnMidToneButton() {
        addActionToUndoList();
    }

    @FXML
    private void clickOnMidToneSlider(MouseEvent mouseEvent) {
        addActionToUndoList();
        this.midTone = midToneSlider.clickOrDrag(mouseEvent);
        modifyImageAndView();
    }

    //******************** OFFSET MŰVELET ***********************************
    @FXML
    private void dragOffsetButton(MouseEvent mouseEvent) {
        this.offset = offsetSlider.clickOrDrag(mouseEvent);
        modifyImageAndView();
    }

    @FXML
    private void pressOnOffsetButton() {
        addActionToUndoList();
    }

    @FXML
    private void clickOnOffsetSlider(MouseEvent mouseEvent) {
        addActionToUndoList();
        this.offset = offsetSlider.clickOrDrag(mouseEvent);
        modifyImageAndView();
    }

    //**************************** UNDO ****************************
    private void addActionToUndoList() {
        if (undoList.size() > UNDOLISTMAXSIZE - 1) undoList.removeLast();
        ModiferValues actualValues = new ModiferValues(equalize, midTone, range, offset);
        if (undoList.isEmpty() || !actualValues.equals(undoList.getFirst())) undoList.addFirst(actualValues);
        undoButton.setDisable(false);
        resetModifiersButton.setDisable(false);
    }

    @FXML
    private void undoAnAction() {
        ModiferValues lastValues = undoList.removeFirst();
        this.equalize = equalizeRadioButton.setRadioButton(lastValues.isEqualize());
        this.midTone = midToneSlider.setSliderValue(lastValues.getMidTone());
        this.range = rangeSlider.setRangeSlider(lastValues.getRange());
        this.offset = offsetSlider.setSliderValue(lastValues.getOffset());
        modifyImageAndView();
        if (undoList.isEmpty()) undoButton.setDisable(true);
        if (range.equals(new IntRange(resizedLumMap.getFirstValue(), resizedLumMap.getLastValue())) &&
                !equalize &&
                midTone == 50 &&
                offset == 0) {
            resetModifiersButton.setDisable(true);
        } else {
            resetModifiersButton.setDisable(false);
        }
    }

    // ******************** MODIFY - VÉGREHAJTÁS, RESET ******************
    private void modifyImageAndView() {
        modifiedLumMap.setSortedItemsValuesTo(resizedLumMap.getSortedItems());

        if (equalize) {
           modifiedLumMap.equalize();
        }
        if (this.offset != 0) {
            modifiedLumMap.offset(this.offset);
        }
        if (modifiedLumMap.getFirstValue() != this.range.min() ||
                modifiedLumMap.getLastValue() != this.range.max()) {
            modifiedLumMap.changeRange(this.range.min(), this.range.max());
        }
        if (this.midTone != 50) {
            modifiedLumMap.changeMidTone(this.midTone);
        }
        //TODO az összeomlást kezelni!!!!
        setRangeToActualValue();
        actualizeImageAndView();
    }

    @FXML
    private void resetModifiers() {
        addActionToUndoList();
        modifiedLumMap.setSortedItemsValuesTo(resizedLumMap.getSortedItems());
        setModifiersToInitialValue();
        setRangeToActualValue();
        actualizeImageAndView();
        resetModifiersButton.setDisable(true);
    }

    private void setSizeLabelsToActualValue() {
        newWidth.setText("" + resizedLumMap.getWidth());
        newHeight.setText("" + resizedLumMap.getHeight());
    }

    private void setModifiersToInitialValue() {
        this.equalize = equalizeRadioButton.setRadioButton(false);
        this.midTone = midToneSlider.setSliderValue(50);
        this.offset = offsetSlider.setSliderValue(0);
        actualizeImageAndView();
    }

    private void setRangeToActualValue() {
        int newMinValue = modifiedLumMap.getFirstValue();
        int newMaxValue = modifiedLumMap.getLastValue();
        this.range.set(newMinValue, newMaxValue);
        rangeSlider.setRangeSlider(newMinValue, newMaxValue);
    }

    private void actualizeImageAndView() {
        WriteImage writeImage = new WriteImage(modifiedLumMap);
        imageView.setImage(writeImage.getWritableImage());
    }

//********************ERROR POPUP WINDOW******************

    private void alertMessage(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ErrorMessage");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);
        alert.show();
    }
//******************** INICIALIZÁLÁS ***********************************

    public void initialize() {
        modifiersPane.setDisable(true);
        keepRatioButton = new StaticRadioButton(keepRatioButtonGroup);
        equalizeRadioButton = new RadioButton(equalizeBackground, equalizeButton);
        midToneSlider = new SingleSlider(0, 100, midToneSliderRail, midToneSliderButton, midToneValue, "%");
        offsetSlider = new SingleSlider(-255, 255, offsetSliderRail, offsetSliderButton, offsetValue, "");
        rangeSlider = new RangeSlider(0, 255, rangeMinButton, rangeMaxButton, rangeSliderRail, rangeSliderRange);


//        midToneSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
//            midTone = newValue.intValue();
//            modifier.getValuesFrom(resizedLuminosity.getSortedItemMap()); //TODO somewhere otherplace
//            modifier.changeMidTone(midTone);
//            modifiedLuminosity = resizedLuminosity.createModifiedLuminosity(modifier);
//            actualizeImageAndView();
//        });

    }

}
