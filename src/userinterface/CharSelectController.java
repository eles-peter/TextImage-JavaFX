package userinterface;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import userinterface.utils.CharPane;
import userinterface.utils.IntRange;
import userinterface.utils.SelectableFlowPane;
import userinterface.utils.UnicodeRange;

public class CharSelectController {

    //TODO ALWAYS ADD SPACE TO THE LIST!!!!!!!!!


    private String selectedFontFamily;
    private IntRange unicodeCharacterRange = new IntRange(33, 127);
    private List<String> selectableList = new ArrayList<>();


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
    }

    @FXML
    private void resetClear() {
        addedFlowPane.getChildren().clear();
    }

    //******************** GENERAL WINDOW FUNCTIONS***********************************
    @FXML
    private void closeWindow() {
        Stage stage = (Stage) charSelectMainPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void createFontCharMap() {


        closeWindow();
    }


    //******************** INICIALIZÁLÁS ***********************************
    public void initialize() {
        initializeSelectableFlowPane();
        initializeAddedFlowPane();
        initializeFontFamilies();
        initializeUnicodeRange();
        fillSelectableListByUnicodeRange(unicodeCharacterRange);
        actualizeSelectablePane();
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
        fontFamiliesSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                this.selectedFontFamily = (String) newValue;
                actualizeSelectablePane();
            }
        });
    }

    private void initializeUnicodeRange() {
        List<String> unicodeRangeNames = Stream.of(UnicodeRange.values())
                .map(Enum -> Enum.longName)
                .collect(Collectors.toList());
        unicodeRangesSelector.getItems().addAll(unicodeRangeNames);
        unicodeRangesSelector.setValue(unicodeRangeNames.get(0));
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


}
