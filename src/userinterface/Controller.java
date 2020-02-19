package userinterface;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.*;

import general.*;
import userinterface.utils.*;
import userinterface.utils.RadioButton;

public class Controller {

    private static final int MAXIMAGESIZE = 200;
    private static final int UNDOLISTMAXSIZE = 20;
    private static final DoubleProperty BASICFONTSIZE = new SimpleDoubleProperty(8);
    private static final double ZOOMRATESTEP = 1.25;

    private String actualFileName;
    private LumMap sourceLumMap;
    private LumMap resizedLumMap;
    private LumMap modifiedLumMap;
    private boolean keepRatio;
    private boolean equalize;
    private boolean inverse;
    private int midTone = 50;
    private IntRange range = new IntRange(0, 255);
    private int offset = 0;
    private Deque<ModifierValues> undoList = new LinkedList<>();
    private boolean showImage;
    private FontCharMapService fontCharMapService;
    private FontCharMap fontCharMap;
    private List<FontCharProperty> charList;
    private ObjectProperty<Color> fontColor = new SimpleObjectProperty<>(Color.BLACK);
    private DoubleProperty zoomRate = new SimpleDoubleProperty();

    //<editor-fold defaultstate="collapsed" desc="ModifierValues class declarations">
    private class ModifierValues {
        private boolean equalize;
        private int midTone;
        private IntRange range;
        private int offset;

        ModifierValues(boolean equalize, int midTone, IntRange range, int offset) {
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
            ModifierValues that = (ModifierValues) o;
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="FXML declarations">
    private Stage primaryStage;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private AnchorPane menuPane;
    @FXML
    private AnchorPane modifiersPane;
    @FXML
    private StackPane imagePane;
    @FXML
    private ScrollPane rasterScrollPane;
    @FXML
    private StackPane rasterPane;
    @FXML
    private Label fileName;
    @FXML
    private ImageView imageView;
    @FXML
    private ImageView imagePreview;
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
    private Rectangle inverseBackground;
    @FXML
    private Rectangle inverseButton;
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
    @FXML
    private Rectangle showImageBackground;
    @FXML
    private Rectangle showImageButton;
    @FXML
    private ChoiceBox fontCharMapSelector;
    @FXML
    private Group zoomGroup;
    //</editor-fold>

    private StaticRadioButton keepRatioButton;
    private RadioButton equalizeRadioButton;
    private RadioButton inverseRadioButton;
    private SingleSlider midToneSlider;
    private SingleSlider offsetSlider;
    private RangeSlider rangeSlider;
    private RadioButton showImageRadioButton;


    @FXML
    private void openFile() {
        File selectedFile = null;
        ReadImageFile rgbimage;
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
        createCharList(modifiedLumMap.getSortedItems(), fontCharMap);

        modifiersPane.setDisable(false);
        modifiersPane.setOpacity(1);
        resetSizeButton.setDisable(true);
        undoButton.setDisable(true);
        resetModifiersButton.setDisable(true);
        zoomRate.setValue(1.0);

        setSizeLabelsToActualValue();
        setModifiersToInitialValue();
        setRangeToActualValue();
        actualizeImageAndView();
        addCharRaster();
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
                if (keepRatio)
                    resizedLumMap = sourceLumMap.resizeToNewKeepRatioBaseWidth(actualNewWidth, baseRatio, MAXIMAGESIZE);
                else resizedLumMap = sourceLumMap.resizeToNew(actualNewWidth, actualHeight, MAXIMAGESIZE);
                resetSizeButton.setDisable(false);
            } catch (IllegalArgumentException e) {
                alertMessage("The maximum values of the rows and columns are " + MAXIMAGESIZE + "!");
            }
        } catch (NumberFormatException e) {
            alertMessage("Please gimme numbers!");
        }
        modifiedLumMap = resizedLumMap.clone();
        createCharList(modifiedLumMap.getSortedItems(), fontCharMap);
        setSizeLabelsToActualValue();
        modifyImageAndView();
        addCharRaster();
    }

    @FXML
    private void resizeImageHeight() {
        try {
            int actualNewHeight = Integer.parseInt(this.newHeight.getText());
            int actualNewWidth = Integer.parseInt(this.newWidth.getText());
            double baseRatio = (double) resizedLumMap.getWidth() / resizedLumMap.getHeight();
            try {
                if (keepRatio)
                    resizedLumMap = sourceLumMap.resizeToNewKeepRatioBaseHeight(actualNewHeight, baseRatio, MAXIMAGESIZE);
                else resizedLumMap = sourceLumMap.resizeToNew(actualNewWidth, actualNewHeight, MAXIMAGESIZE);
                resetSizeButton.setDisable(false);
            } catch (IllegalArgumentException e) {
                alertMessage("The maximum values of the rows and columns are " + MAXIMAGESIZE + "!");
            }
        } catch (NumberFormatException e) {
            alertMessage("Please gimme numbers!");
        }
        modifiedLumMap = resizedLumMap.clone();
        createCharList(modifiedLumMap.getSortedItems(), fontCharMap);
        setSizeLabelsToActualValue();
        modifyImageAndView();
        addCharRaster();
    }

    @FXML
    private void resetImageSize() {
        resizedLumMap = sourceLumMap.clone();
        modifiedLumMap = resizedLumMap.clone();
        resetSizeButton.setDisable(true);
        setSizeLabelsToActualValue();
        modifyImageAndView();
        addCharRaster();
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

    //************************INVERSE**************************************
    @FXML
    private void clickedInverseButton() {
        this.inverse = inverseRadioButton.changeRadioButton(this.inverse);
        if (inverse) {
            this.rasterPane.setStyle("-fx-background-color: #000000;");
            this.fontColor.setValue(Color.WHITE);
        } else {
            this.rasterPane.setStyle("-fx-background-color: #FFFFFF;");
            this.fontColor.setValue(Color.BLACK);
        }
        actualizeCharList();
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
        ModifierValues actualValues = new ModifierValues(equalize, midTone, range, offset);
        if (undoList.isEmpty() || !actualValues.equals(undoList.getFirst())) undoList.addFirst(actualValues);
        undoButton.setDisable(false);
        resetModifiersButton.setDisable(false);
    }

    @FXML
    private void undoAnAction() {
        ModifierValues lastValues = undoList.removeFirst();
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
        actualizeCharList();

    }

    @FXML
    private void resetModifiers() {
        addActionToUndoList();
        modifiedLumMap.setSortedItemsValuesTo(resizedLumMap.getSortedItems());
        setModifiersToInitialValue();
        setRangeToActualValue();
        actualizeImageAndView();
        actualizeCharList();
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
        imagePreview.setImage(writeImage.getWritableImage());

        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(imagePane.widthProperty());
        imageView.fitHeightProperty().bind(imagePane.heightProperty());
    }

    //************************ SHOW IMAGE **************************************
    @FXML
    private void clickedShowImageButton() {
        this.showImage = showImageRadioButton.changeRadioButton(this.showImage);
        if (showImage) {
            rasterScrollPane.setVisible(false);
            imagePane.setVisible(true);
            zoomGroup.setDisable(true);
        } else {
            imagePane.setVisible(false);
            rasterScrollPane.setVisible(true);
            zoomGroup.setDisable(false);
        }
    }

//******************** CHARTABLE ******************


    //TODO fontfamily bekötése
    //TODO oldalszélesség bekötésse???? de az inkább később....(újragenerálás)
    @FXML
    private void addCharRaster() {
        double charHeight = BASICFONTSIZE.getValue();
        double charWidth = BASICFONTSIZE.getValue(); //TODO megszorozni az arányszámmal...
        Group charTableGroup = new Group();
        charTableGroup.setId("CharRasterGroup");
        int width = modifiedLumMap.getWidth();
        int height = modifiedLumMap.getHeight();
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                Lum actualLum = modifiedLumMap.getLumArray()[h][w];
                int actualIndex = modifiedLumMap.getSortedItems().indexOf(actualLum);
                FontCharProperty actualFontChar = this.charList.get(actualIndex);
                SimpleStringProperty actualChar = actualFontChar.unicodeCharProperty();
                SimpleStringProperty actualFontFamily = actualFontChar.fontFamilyProperty();
                Text actualText = new Text();
                actualText.textProperty().bind(actualChar);
                actualText.fillProperty().bind(fontColor);
                actualText.fontProperty().bind(Bindings.createObjectBinding(() -> Font.font(actualFontFamily.getValue(), BASICFONTSIZE.getValue()), actualFontFamily, BASICFONTSIZE));
//                actualText.setFont(Font.font("Consolas", BASICFONTSIZE.getValue()));
                actualText.setLayoutX(charWidth * w); //TODO kiszámítani a koordinátákat
                actualText.setLayoutY(charHeight * h);
                charTableGroup.getChildren().add(actualText);
            }
        }
        rasterPane.getChildren().clear();
        zoomRate.setValue(1.0);
        charTableGroup.scaleXProperty().bind(zoomRate);
        charTableGroup.scaleYProperty().bind(zoomRate);
        rasterPane.getChildren().add(charTableGroup);
        rasterPane.minWidthProperty().bind(Bindings.max(Bindings.multiply(charTableGroup.getBoundsInLocal().getWidth(), zoomRate), rasterScrollPane.widthProperty()));
        rasterPane.minHeightProperty().bind(Bindings.max(Bindings.multiply(charTableGroup.getBoundsInLocal().getHeight(), zoomRate), rasterScrollPane.heightProperty()));
    }

    //******************** ZOOM ACTION ******************
    //TODO try it with callback function!
    @FXML
    private void zoomIn() {
        zoomRate.setValue(zoomRate.getValue() * ZOOMRATESTEP);
    }

    @FXML
    private void zoomOut() {
        zoomRate.setValue(zoomRate.getValue() / ZOOMRATESTEP);
    }

    @FXML
    private void zoomActualSize() {
        zoomRate.setValue(1.0);
    }

    @FXML
    private void zoomFitToWindow() {
        double charTableGroupWidth = rasterPane.getChildren().get(0).getBoundsInLocal().getWidth();
        double charTableGroupHeight = rasterPane.getChildren().get(0).getBoundsInLocal().getHeight();
        double zoomRateFitWidth = rasterScrollPane.getWidth() / charTableGroupWidth;
        double zoomRateFitHeight = rasterScrollPane.getHeight() / charTableGroupHeight;
        zoomRate.setValue(Math.min(zoomRateFitHeight, zoomRateFitWidth));
    }

    @FXML
    private void setScrollToCenter() {
        rasterScrollPane.setVvalue(0.5);
        rasterScrollPane.setHvalue(0.5);
    }

    //******************** CHAR LIST ******************
    //TODO kivenni a constructorból a fieldeket!!!
    private void createCharList(List<Lum> lumList, FontCharMap fontCharMap) {
        this.charList = new ArrayList<>();
        for (Lum lum : lumList) {
            int luminosityValue = lum.getValue();
            FontChar actualFontChar = fontCharMap.get(luminosityValue);
            FontCharProperty fontCharProperty = new FontCharProperty(actualFontChar);
            this.charList.add(fontCharProperty);
        }
    }

    private void actualizeCharList() {
        List<Lum> actualLumList = modifiedLumMap.getSortedItems();
        for (int i = 0; i < this.charList.size(); i++) {
            int actualLumValue = actualLumList.get(i).getValue();
            if (inverse) {
                actualLumValue = 255 - actualLumValue;
            }
            FontChar actualFontChar = fontCharMap.get(actualLumValue);
            this.charList.get(i).setFontChar(actualFontChar);
        }
    }

    //******************** CREATE NEW CHARACTER SET ******************

    @FXML
    private void newCharSet() throws IOException {
        final Stage charSelectStage = new Stage();
        charSelectStage.initModality(Modality.APPLICATION_MODAL);
        charSelectStage.setTitle("Create new CharacterSet");
        charSelectStage.setResizable(false);
        Parent root = FXMLLoader.load(getClass().getResource("charSelectStage.fxml"));
        charSelectStage.setScene(new Scene(root, 800, 480));
        charSelectStage.show();

    }

    @FXML
    private void modifyCharSet() throws IOException {
        fontCharMapService.setIsModify(true);
        newCharSet();
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
        inverseRadioButton = new RadioButton(inverseBackground, inverseButton);
        midToneSlider = new SingleSlider(0, 100, midToneSliderRail, midToneSliderButton, midToneValue, "%");
        offsetSlider = new SingleSlider(-255, 255, offsetSliderRail, offsetSliderButton, offsetValue, "");
        rangeSlider = new RangeSlider(0, 255, rangeMinButton, rangeMaxButton, rangeSliderRail, rangeSliderRange);
        showImageRadioButton = new RadioButton(showImageBackground, showImageButton);
        this.showImage = showImageRadioButton.setRadioButton(false);

        fontCharMapService = FontCharMapService.getInstance();
        fontCharMapService.initialize();
        List<String> FCMNameList = fontCharMapService.getFCMNameList();
        fontCharMapSelector.getItems().addAll(FCMNameList);
        fontCharMapSelector.setValue(fontCharMapService.getActualFCMName());
        this.fontCharMap = fontCharMapService.getActualFontCharMap();
        fontCharMapSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                fontCharMapService.setActualFCMName((String) newValue);
                this.fontCharMap = fontCharMapService.getActualFontCharMap();
                actualizeCharList();
            }
        });
        fontCharMapService.isChangedProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue) && newValue) {
                String newFCMName = fontCharMapService.getActualFCMName();
                List<String> oldFCMNameList = fontCharMapSelector.getItems();
                if (!oldFCMNameList.contains(newFCMName)) {
                    fontCharMapSelector.getItems().add(newFCMName);
                }
                fontCharMapSelector.setValue(newFCMName);
                this.fontCharMap = fontCharMapService.getActualFontCharMap(); // lehet, hogy nem kell az előbbi listener miatt...
                actualizeCharList();
                fontCharMapService.setIsChanged(false);
            }
        });

        rasterPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                setScrollToCenter();
            }
        });


//        rasterScrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
//            scrollV.setText(newValue.toString());
//        });
//        rasterScrollPane.hvalueProperty().addListener((observable, oldValue, newValue) -> {
//            scrollH.setText(newValue.toString());
//        });
//
//        zoomRate.addListener((observable, oldValue, newValue) -> {
//            zoomRateLabel.setText(newValue.toString());
//            rasterScrollPane.setVvalue(rasterScrollPane.getVvalue() * (oldValue.doubleValue()/newValue.doubleValue()));
//            rasterScrollPane.setHvalue(rasterScrollPane.getHvalue() * (oldValue.doubleValue()/newValue.doubleValue()));
//        });


//        midToneSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
//            midTone = newValue.intValue();
//            modifier.getValuesFrom(resizedLuminosity.getSortedItemMap()); //TODO somewhere otherplace
//            modifier.changeMidTone(midTone);
//            modifiedLuminosity = resizedLuminosity.createModifiedLuminosity(modifier);
//            actualizeImageAndView();
//        });

    }

}
