package userInterface;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TemporaryUtils {




    @FXML
    public void popupErrorWindow(String newErrorMessage) {
        final Stage errorMessageStage = new Stage();
        errorMessageStage.initModality(Modality.APPLICATION_MODAL);
        errorMessageStage.setTitle("ErrorMessage");
        errorMessageStage.setResizable(false);

        Label errorMessageLabel = new Label(newErrorMessage);
        errorMessageLabel.setText(newErrorMessage);
        errorMessageLabel.setStyle(" -fx-alignment: CENTER; -fx-content-display: CENTER; -fx-text-alignment: CENTER; -fx-wrap-text: true;");
        errorMessageLabel.setLayoutY(10);
        errorMessageLabel.setPrefHeight(65);
        errorMessageLabel.setPrefWidth(230);

        Button errorMessageButton = new Button("OK");
        errorMessageButton.setLayoutX(109);
        errorMessageButton.setLayoutY(80);
        errorMessageButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Is working");
                errorMessageStage.close();
            }
        });

        AnchorPane errorMessageRoot = new AnchorPane();
        errorMessageRoot.setStyle("-fx-pref-width: 250.0; -fx-pref-height: 120.0;");
        errorMessageStage.setScene(new Scene(errorMessageRoot, 250, 120));

        errorMessageRoot.getChildren().addAll(errorMessageButton, errorMessageLabel);

        errorMessageStage.show();

//        errorMessageStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//            @Override
//            public void handle(final WindowEvent event) {
//                mainPane.setDisable(false);
//            }
//        });
    }

}
