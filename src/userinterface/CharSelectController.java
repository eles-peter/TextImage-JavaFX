package userinterface;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import general.FontChar;
import general.FontCharMap;
import userinterface.utils.CharPane;
import userinterface.utils.IntRange;
import userinterface.utils.SelectableFlowPane;
import userinterface.utils.UnicodeRange;

public class CharSelectController {

    private String selectedFontFamily;
    private IntRange unicodeCharacterRange = new IntRange();
    private List<String> selectableList = new ArrayList<>();
    private IntegerProperty timer = new SimpleIntegerProperty(0);

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
    private void createOK() {
        int evaluatorboxsize = 120;
        StackPane evaluatorPane = new StackPane();
        evaluatorPane.setStyle(" -fx-pref-height: evaluatorboxsize; -fx-pref-width: evaluatorboxsize; -fx-background-color: #FFFFFF; -fx-effect: dropshadow(three-pass-box, darkgray, 20, 0, 0, 0);");
        AnchorPane.setTopAnchor(evaluatorPane, 150.0);
        AnchorPane.setRightAnchor(evaluatorPane, 138.0);
        charSelectMainPane.getChildren().add(evaluatorPane);

        Canvas canvas = new Canvas(evaluatorboxsize, evaluatorboxsize);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        evaluatorPane.getChildren().add(canvas);

        FontCharMap fontCharMap = new FontCharMap();
        //TODO check the newName is not only whitespace...(possible to make a filename from it...)
        fontCharMap.setName(newName.getText());
        final double[] maxWidth = {0};

        timer.setValue(addedFlowPane.getChildren().size() - 1);
        Timeline animatedEvaluator = new Timeline(new KeyFrame(Duration.millis(200), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gc.clearRect(0, 0, evaluatorboxsize, evaluatorboxsize);
                int index = timer.getValue();
                CharPane charPane = (CharPane) addedFlowPane.getChildren().remove(index);
                FontChar actualFontChar = charPane.getFontChar();
                Font actualFont = Font.font(actualFontChar.getFontFamily(), 60);
                gc.setFont(actualFont);
                gc.fillText(actualFontChar.getUnicodeChar(), evaluatorboxsize/2, evaluatorboxsize/2);

                double actualCharWidth = charPane.getText().getBoundsInLocal().getWidth();
                maxWidth[0] = Math.max(maxWidth[0], actualCharWidth);




                timer.setValue(index - 1);
            }
        }));

        animatedEvaluator.setCycleCount(Timeline.INDEFINITE);
        timer.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                if (newValue.intValue() < 0) {
                    animatedEvaluator.stop();


                    //TODO ALWAYS ADD SPACE TO THE LIST!!!!!!!!!

                    closeWindow();
                }
            }
        });
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


}
