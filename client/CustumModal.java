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
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(15), Insets.EMPTY)));
        root.setEffect(new DropShadow(10, Color.gray(0.3)));
        root.setPrefWidth(400);
        root.setPrefHeight(250);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", 20));
        titleLabel.setTextFill(Color.web("#185a9d"));

        Label messageLabel = new Label(message);
        messageLabel.setFont(Font.font("Segoe UI", 16));
        messageLabel.setTextFill(Color.web("#333"));
        messageLabel.setWrapText(true); // Për ta bërë tekstin me rreshta

        Button okButton = new Button("OK");
        okButton.setFont(Font.font(16));
        okButton.setStyle("-fx-background-radius: 20; -fx-background-color: #43cea2; -fx-text-fill: white;");
        okButton.setPrefWidth(100);
        okButton.setOnAction(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), root);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(ev -> dialog.close());
            fadeOut.play();
        });

        root.getChildren().addAll(titleLabel, messageLabel, okButton);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        dialog.setScene(scene);

        // Poziciono në qendër të dritares prind
        dialog.setOnShowing(e -> {
            Window ownerWindow = dialog.getOwner();
            if (ownerWindow != null) {
                double centerX = ownerWindow.getX() + ownerWindow.getWidth() / 2;
                double centerY = ownerWindow.getY() + ownerWindow.getHeight() / 2;

                double dialogWidth = 400;
                double dialogHeight = 250;

                dialog.setWidth(dialogWidth);
                dialog.setHeight(dialogHeight);

                dialog.setX(centerX - dialogWidth / 2);
                dialog.setY(centerY - dialogHeight / 2);
            }
        });

        // Fade in effect
        root.setOpacity(0);
        dialog.show();
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
}
