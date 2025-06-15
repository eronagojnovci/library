package client;

import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.*;
import javafx.util.Duration;

class CustomModal {

    public static void show(Stage owner, String title, String message) {
        // Overlay semi-transparent
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");
        overlay.setPrefSize(owner.getWidth(), owner.getHeight());

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.web("#f5f0e6"), new CornerRadii(15), Insets.EMPTY))); // beige ngjyrë
        root.setEffect(new DropShadow(15, Color.gray(0.3)));
        root.setPrefWidth(350);
        root.setMaxWidth(350);
        root.setPrefHeight(220);   // Lartësia e modale më e vogël
        root.setMaxHeight(220);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI Semibold", 22));
        titleLabel.setTextFill(Color.web("#8b6c49")); // ngjyrë më e ngrohtë

        Label messageLabel = new Label(message);
        messageLabel.setFont(Font.font("Segoe UI", 15));
        messageLabel.setTextFill(Color.web("#6b5e4a")); // ngjyrë beige-kafe
        messageLabel.setWrapText(true);
        messageLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        messageLabel.setMaxWidth(300);

        Button okButton = new Button("OK");
        okButton.setFont(Font.font("Segoe UI", 15));
        okButton.setStyle(
                "-fx-background-radius: 20;" +
                        "-fx-background-color: linear-gradient(to right, #d7c49e, #bfa86a);" +  // gradient beige
                        "-fx-text-fill: #4a3b1f;" +
                        "-fx-padding: 6 20 6 20;"
        );
        okButton.setOnMouseEntered(e -> okButton.setStyle(
                "-fx-background-radius: 20;" +
                        "-fx-background-color: linear-gradient(to right, #bfa86a, #d7c49e);" +
                        "-fx-text-fill: #4a3b1f;" +
                        "-fx-padding: 6 20 6 20;"
        ));
        okButton.setOnMouseExited(e -> okButton.setStyle(
                "-fx-background-radius: 20;" +
                        "-fx-background-color: linear-gradient(to right, #d7c49e, #bfa86a);" +
                        "-fx-text-fill: #4a3b1f;" +
                        "-fx-padding: 6 20 6 20;"
        ));

        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);

        overlay.getChildren().add(root);
        StackPane.setAlignment(root, Pos.CENTER);

        Scene scene = new Scene(overlay);
        scene.setFill(Color.TRANSPARENT);

        dialog.setScene(scene);

        // Poziciono dialogun sipas owner
        dialog.setX(owner.getX());
        dialog.setY(owner.getY());
        dialog.setWidth(owner.getWidth());
        dialog.setHeight(owner.getHeight());

        // Animacion fade in për modalin
        root.setOpacity(0);
        dialog.show();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        okButton.setOnAction(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(250), root);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(ev -> dialog.close());
            fadeOut.play();
        });

        root.getChildren().addAll(titleLabel, messageLabel, okButton);
    }
}
