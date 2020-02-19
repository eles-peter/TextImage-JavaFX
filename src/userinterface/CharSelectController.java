package userinterface;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import general.FontChar;
import general.FontCharMap;
import userinterface.utils.CharPane;
import userinterface.utils.FontCharMapService;
import userinterface.utils.IntRange;
import userinterface.utils.SelectableFlowPane;
import userinterface.resources.UnicodeRange;

public class CharSelectController {

    private String selectedFontFamily;
    private IntRange unicodeCharacterRange = new IntRange();
    private List<String> selectableList = new ArrayList<>();
    private Timeline animatedEvaluator;
    private Set<String> fontFamilySet = new HashSet<>();
    private FontCharMap fontCharMap;
    private FontCharMapService fontCharMapService;
    private double fontMaxWidth;

    @FXML
    private AnchorPane charSelectMainPane;
    @FXML
    private ChoiceBox fontFamiliesSelector;
    @FXML
    private ChoiceBox unicodeRangesSelector;
    @FXML
    private ScrollPane selectableScrollPane;
    @FXML
    private ScrollPane addedScrollPane;
    @FXML
    private Label addedCharacters;
    @FXML
    private TextField newName;

    private SelectableFlowPane selectableFlowPane = new SelectableFlowPane();
    private SelectableFlowPane addedFlowPane = new SelectableFlowPane();


    private void actualizeSelectablePane() {
        selectableFlowPane.getChildren().clear();
        selectableFlowPane.clearLastSelected();
        for (String actualChar : selectableList) {
            CharPane charPane = new CharPane(actualChar, selectedFontFamily);
            selectableFlowPane.getChildren().add(charPane);
        }
    }

    @FXML
    private void deselectAllSelectablePane() {
        for (Node charPaneNode : selectableFlowPane.getChildren()) {
            CharPane selectedPane = (CharPane) charPaneNode;
            selectedPane.setIsSelectFalse();
        }
        selectableFlowPane.clearLastSelected();
    }

    @FXML
    private void selectAllSelectablePane() {
        for (Node charPaneNode : selectableFlowPane.getChildren()) {
            CharPane selectedPane = (CharPane) charPaneNode;
            selectedPane.setIsSelectTrue();
        }
        selectableFlowPane.clearLastSelected();
    }

    @FXML
    private void add() {
        for (Node charPaneNode : selectableFlowPane.getChildren()) {
            CharPane charPane = (CharPane) charPaneNode;
            if (charPane.isSelected() && !addedFlowPane.getChildren().contains(charPane)) {
                addedFlowPane.getChildren().add(charPane.copy());
            }
        }
        deselectAllSelectablePane();
        actualizeAddedCharactersLabel();
    }

    @FXML
    private void remove() {
        for (int i = 0; i < addedFlowPane.getChildren().size(); i++) {
            CharPane charPane = (CharPane) addedFlowPane.getChildren().get(i);
            if (charPane.isSelected()) {
                addedFlowPane.getChildren().remove(i);
                i--;
            }
        }
        actualizeAddedCharactersLabel();
    }

    @FXML
    private void resetClear() {
        addedFlowPane.getChildren().clear();
    }

    @FXML
    private void actualizeAddedCharactersLabel() {
        int newValue = addedFlowPane.getChildren().size();
        addedCharacters.setText("Added Characters (" + newValue + " added)");
    }

    //******************** GENERAL WINDOW FUNCTIONS***********************************
    @FXML
    private void closeWindow() {
        Stage stage = (Stage) charSelectMainPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void createOKAndSave() {
        create(true);
    }

    @FXML
    private void createAndOK() {
        create(false);
    }

    private void create(Boolean isSaved) {
        StackPane evaluatorPane = new StackPane();
        evaluatorPane.setStyle(" -fx-min-height: 120; -fx-min-width: 120; -fx-background-color: #FFFFFF; -fx-effect: dropshadow(three-pass-box, darkgray, 20, 0, 0, 0);");
        AnchorPane.setTopAnchor(evaluatorPane, 150.0);
        AnchorPane.setRightAnchor(evaluatorPane, 138.0);
        charSelectMainPane.getChildren().add(evaluatorPane);

        String fontCharMapName = newName.getText();
        if (!fontCharMapName.matches("[\\w,-]{3,}")) {
            alertMessage("Please gimme correct FileName! \n (no whitespace please!)");
            return;
        }
        if (fontCharMapService.getFCMbyName(fontCharMapName) != null) {
            fontCharMap = fontCharMapService.getFCMbyName(fontCharMapName);
            fontCharMap.clearFontChars();
        } else {
            fontCharMap = new FontCharMap();
            fontCharMap.setName(fontCharMapName);
        }

        //TODO refractor kiemelni metódusba
        animatedEvaluator = new Timeline(new KeyFrame(Duration.millis(20), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (addedFlowPane.getChildren().size() > 0) {
                    evaluatorPane.getChildren().clear();
                     CharPane charPane = (CharPane) addedFlowPane.getChildren().remove(0);
                    FontChar actualFontChar = charPane.getFontChar();
                    fontFamilySet.add(actualFontChar.getFontFamily());
                    Font actualFont = Font.font(actualFontChar.getFontFamily(), 60);
                    Text actualText = new Text(actualFontChar.getUnicodeChar());
                    actualText.setFont(actualFont);
                    evaluatorPane.getChildren().add(actualText);

                    double actualCharWidth = actualText.getBoundsInLocal().getWidth();
                    fontMaxWidth = Math.max(fontMaxWidth, actualCharWidth);
                    //TODO kiértékelésen gondolkodni, megnézni, mi volt Python-ban!!!
                    WritableImage snapshot = actualText.snapshot(new SnapshotParameters(), null);
                    int width = (int) snapshot.getWidth();
                    int height = (int) snapshot.getHeight();
                    PixelReader pixelReader = snapshot.getPixelReader();
                    double inversePixelValue = 0.0;
                    for (int h = 0; h < height; h++) {
                        for (int w = 0; w < width; w++) {
                            Color pixelColor = pixelReader.getColor(w, h);
                            double red = pixelColor.getRed();
                            double green = pixelColor.getGreen();
                            double blue = pixelColor.getBlue();
                            inversePixelValue+= (1 - red) + (1 - green) + (1 - blue);
                        }
                    }
                    fontCharMap.add((int)inversePixelValue, actualFontChar);

                } else {
                    animatedEvaluator.stop();
                    fontCharMap.add(0, " ", fontFamilySet.iterator().next());

                    double maxHeight = 0.0;
                    for (String fontFamily : fontFamilySet) {
                        Text actualText = new Text(Character.toString((char)2588));
                        actualText.setFont(Font.font(fontFamily, 60));
                        double actualHeight = actualText.getBoundsInLocal().getHeight();
                        maxHeight = Math.max(maxHeight, actualHeight);
                    }
                    fontCharMap.setMaxHeight(maxHeight);
                    fontCharMap.setMaxWidth(fontMaxWidth);
                    fontCharMap.changeRange(255,0);
                    fontCharMap.fillTheGap();

                    fontCharMapService.addFontCharMapAndSetActual(fontCharMap);
                    fontCharMapService.setIsChanged(true);

                    if (isSaved) {
                        try {
                            fontCharMap.writeToFile("src\\userinterface\\resources\\" + fontCharMap.getName() + ".fcm");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    closeWindow();
                }
            }
        }, new javafx.animation.KeyValue[]{}));

        animatedEvaluator.setCycleCount(Timeline.INDEFINITE);
        animatedEvaluator.play();

    }


    //******************** INICIALIZÁLÁS ***********************************
    public void initialize() {
        initializeSelectableFlowPane();
        initializeAddedFlowPane();
        initializeFontFamilies();
        initializeUnicodeRange();
        fillSelectableListByUnicodeRange(unicodeCharacterRange);
        actualizeSelectablePane();
        fontCharMapService = FontCharMapService.getInstance();
        if (fontCharMapService.getIsModify()) {
            loadActualFontCharMap();
            fontCharMapService.setIsModify(false);
        }
    }

    private void initializeSelectableFlowPane() {
        selectableFlowPane.setStyle("-fx-hgap: 1.0; -fx-max-width: 311.0; -fx-pref-width: 311.0; fx-min-width: 311.0; -fx-pref-height: 342.0; -fx-background-color: #D3D3D3; -fx-vgap: 1.0; -fx-row-valignment: TOP;");
        selectableScrollPane.setContent(selectableFlowPane);
    }

    private void initializeAddedFlowPane() {
        addedFlowPane.setStyle("-fx-hgap: 1.0; -fx-max-width: 311.0; -fx-pref-width: 311.0; fx-min-width: 311.0; -fx-pref-height: 342.0; -fx-background-color: #D3D3D3; -fx-vgap: 1.0; -fx-row-valignment: TOP;");
        addedScrollPane.setContent(addedFlowPane);
    }

    private void initializeFontFamilies() {
        List<String> fontFamilies = Font.getFamilies();
        fontFamiliesSelector.getItems().addAll(fontFamilies);
        fontFamiliesSelector.setValue(fontFamilies.get(0));
        this.selectedFontFamily = fontFamilies.get(0);
        fontFamiliesSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                this.selectedFontFamily = (String) newValue;
                actualizeSelectablePane();
            }
        });
    }

    private void initializeUnicodeRange() {
        List<String> unicodeRangeNames = Stream.of(UnicodeRange.values())
                .map(e -> e.longName)
                .collect(Collectors.toList());
        unicodeRangesSelector.getItems().addAll(unicodeRangeNames);
        String baseValue = unicodeRangeNames.get(0);
        unicodeRangesSelector.setValue(baseValue);
        this.unicodeCharacterRange = rangeFromUnicodeEnum(baseValue);

        unicodeRangesSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                this.unicodeCharacterRange = rangeFromUnicodeEnum((String) newValue);
                fillSelectableListByUnicodeRange(unicodeCharacterRange);
                actualizeSelectablePane();
            }
        });
    }

    private IntRange rangeFromUnicodeEnum(String unicodeRangeName) {
        UnicodeRange selectedUnicodeRange = UnicodeRange.getEnumByName(unicodeRangeName);
        int rangeMin = selectedUnicodeRange.rangeMin;
        int rangeMax = selectedUnicodeRange.rangeMax;
        return new IntRange(rangeMin, rangeMax);
    }

    private void fillSelectableListByUnicodeRange(IntRange unicodeRange) {
        selectableList.clear();
        for (int unicodeValue = unicodeRange.getMinValue(); unicodeValue <= unicodeRange.getMaxValue(); unicodeValue++) {
            String actualChar = Character.toString((char) unicodeValue);
            selectableList.add(actualChar);
        }
    }

    private void loadActualFontCharMap() {
        newName.setText(fontCharMapService.getActualFCMName());
        FontCharMap fontCharMapToEdit = fontCharMapService.getActualFontCharMap();
        for(Map.Entry<Integer, FontChar> entry : fontCharMapToEdit.getFontChars().entrySet()) {
            FontChar fontChar = entry.getValue();
            String unicodeChar = fontChar.getUnicodeChar();
            String fontFamily = fontChar.getFontFamily();
            CharPane charPane = new CharPane(unicodeChar, fontFamily);
            if (!addedFlowPane.getChildren().contains(charPane)) {
                addedFlowPane.getChildren().add(charPane);
            }
        }
    }

    private void alertMessage(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ErrorMessage");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);
        alert.show();
    }


}
