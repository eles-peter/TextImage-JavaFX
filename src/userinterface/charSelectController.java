package userinterface;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import userinterface.utils.IntRange;
import userinterface.utils.UnicodeRange;

public class charSelectController {

    //TODO ALWAYS ADD SPACE TO THE LIST!!!!!!!!!

    private static final int FONTSIZE = 15;
    private static final int FONTPANELSIZE = 30;

    private String selectedFontFamily;
    private IntRange unicodeCharacterRange = new IntRange(33, 127);
    private List<String> selectableList = new ArrayList<>();
    private List<String> selectedSelectableList = new ArrayList<>();

    private List<String> addedList = new ArrayList<>();
    private List<String> selectedAddedList = new ArrayList<>();


    @FXML
    private AnchorPane charSelectMainPane;
    @FXML
    private ChoiceBox fontFamiliesSelector;
    @FXML
    private ChoiceBox unicodeRangesSelector;
    @FXML
    private FlowPane selectableFlowPane;
    @FXML
    private FlowPane addedFlowPane;


    private void actualizeListInSelectablePane() {
        selectableFlowPane.getChildren().clear();
        selectedSelectableList.clear();
        for (String actualChar : selectableList) {
            Text actualText = new Text();
            actualText.setFont(Font.font(selectedFontFamily, FONTSIZE));
            actualText.setText(actualChar);
            StackPane charPane = new StackPane(actualText);
            charPane.setStyle("-fx-background-color: #FFFFFF;");
            charPane.setPrefSize(FONTPANELSIZE, FONTPANELSIZE);
            charPane.setOnMouseClicked((event) -> {
                if (event.isShiftDown()) {
                    multiSelectSelectablePane(charPane);
                } else {
                    selectSelectablePane(charPane);
                }
            });
            selectableFlowPane.getChildren().add(charPane);
        }
    }

    //TODO lastselected, deselect all method
    @FXML
    private void deselectAllSelectablePane() {
        selectedSelectableList.clear();
        for (Node charPaneNode : selectableFlowPane.getChildren()) {
            Pane selectedPane = (Pane) charPaneNode;
            selectedPane.setStyle("-fx-background-color: #FFFFFF;");
            selectedPane.getChildren().get(0).setStyle("-fx-fill: #000000;");
        }
    }

    @FXML
    private void selectAllSelectablePane() {
        selectedSelectableList.clear();
        for (Node charPaneNode : selectableFlowPane.getChildren()) {
            Pane selectedPane = (Pane) charPaneNode;
            Text selectedText = (Text) selectedPane.getChildren().get(0);
            String selectedChar = selectedText.getText();
            selectedSelectableList.add(selectedChar);
            selectedPane.setStyle("-fx-background-color: #00BFFF;");
            selectedPane.getChildren().get(0).setStyle("-fx-fill: #FFFFFF");
        }
    }

    private void selectSelectablePane(Pane selectedPane) {
        Text selectedText = (Text) selectedPane.getChildren().get(0);
        String selectedChar = selectedText.getText();
        if (selectedSelectableList.contains(selectedChar)) {
            selectedSelectableList.remove(selectedChar);
            selectedPane.setStyle("-fx-background-color: #FFFFFF;");
            selectedPane.getChildren().get(0).setStyle("-fx-fill: #000000");
        } else {
            selectedSelectableList.add(selectedChar);
            selectedPane.setStyle("-fx-background-color: #00BFFF;");
            selectedPane.getChildren().get(0).setStyle("-fx-fill: #FFFFFF");
        }
    }

    //TODO ez így nem lesz jó, ha kiveszem azokat a karaktereket amik nicsenek a karakterkészletben!!!
    private void multiSelectSelectablePane(Pane selectedPane) {
        if (selectedSelectableList.isEmpty()) {
            selectSelectablePane(selectedPane);
        } else {
            Text selectedText = (Text) selectedPane.getChildren().get(0);
            String selectedChar = selectedText.getText();
            int indexA = selectableList.indexOf(selectedChar);
            String lastSelectedChar = selectedSelectableList.get(selectedSelectableList.size() - 1);
            int indexB = selectableList.indexOf(lastSelectedChar);
            int start = Math.min(indexA, indexB +1);
            int end = Math.max(indexA, indexB -1);
            for (int i = start; i <= end; i++) {
                Pane actualSelectedPane = (Pane) selectableFlowPane.getChildren().get(i);
                selectSelectablePane(actualSelectedPane);
            }
        }
    }

    //******************** ADDED PANE ***********************************

    @FXML
    private void add() {
        for (String selectedChar : selectedSelectableList) {
            if (!addedList.contains(selectedChar)) {
                addedList.add(selectedChar);
            }
        }
        deselectAllSelectablePane();
        actualizeListInAddedPane();
    }

    //TODO átírni a selectedaddedlsitet!
    private void actualizeListInAddedPane() {
        addedFlowPane.getChildren().clear();
        for (String actualChar : addedList) {
            Text actualText = new Text();
            actualText.setFont(Font.font(selectedFontFamily, FONTSIZE));
            actualText.setText(actualChar);
            StackPane charPane = new StackPane(actualText);
            charPane.setStyle("-fx-background-color: #FFFFFF;");
            charPane.setPrefSize(FONTPANELSIZE, FONTPANELSIZE);
            charPane.setOnMouseClicked((event) -> {
                if (event.isShiftDown()) {
                    multiSelectSelectablePane(charPane);
                } else {
                    selectSelectablePane(charPane);
                }
            });
            addedFlowPane.getChildren().add(charPane);
        }
    }



    //******************** GENERAL WINDOW ***********************************

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) charSelectMainPane.getScene().getWindow();
        stage.close();
    }


    //******************** INICIALIZÁLÁS ***********************************
    public void initialize() {
        initializeFontFamilies();
        initializeUnicodeRange();
        fillSelectableListByUnicodeRange(unicodeCharacterRange);
        actualizeListInSelectablePane();


    }

    private void initializeFontFamilies() {
        List<String> fontFamilies = Font.getFamilies();
        fontFamiliesSelector.getItems().addAll(fontFamilies);
        fontFamiliesSelector.setValue(fontFamilies.get(0));
        fontFamiliesSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                this.selectedFontFamily = (String)newValue;
                System.out.println(selectedFontFamily);
                actualizeListInSelectablePane();
            }
        });
    }

    private void initializeUnicodeRange() {
        List<String> unicodeRangeNames = Stream.of(UnicodeRange.values())
                .map(Enum->Enum.longName)
                .collect(Collectors.toList());
        unicodeRangesSelector.getItems().addAll(unicodeRangeNames);
        unicodeRangesSelector.setValue(unicodeRangeNames.get(0));
        unicodeRangesSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                this.unicodeCharacterRange = rangeFromUnicodeEnum((String)newValue);
                fillSelectableListByUnicodeRange(unicodeCharacterRange);
                actualizeListInSelectablePane();
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
