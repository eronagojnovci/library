package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class ClientGUI extends Application {

    private FlowPane cardsPane;
    private TextField searchField, bookIdField;
    private TextArea outputArea;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showWelcome(primaryStage);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void showWelcome(Stage stage) {
        VBox welcomeBox = new VBox(20);
        welcomeBox.setAlignment(Pos.CENTER);
        welcomeBox.setPadding(new Insets(150, 40, 60, 40));
        BackgroundImage bgImage = new BackgroundImage(
                new javafx.scene.image.Image(getClass().getResource("/assets/lb.jpg").toExternalForm()),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(100, 100, true, true, false, true)
        );
        welcomeBox.setBackground(new Background(bgImage));

        Label title = new Label("Welcome to our Library!");
        title.setFont(Font.font("Segoe UI", 38));
        title.setTextFill(Color.web("#ffffff"));
        Label subtitle = new Label("Explore, borrow and enjoy your favorite books.");
        subtitle.setFont(Font.font("Segoe UI", 20));
        subtitle.setTextFill(Color.web("#ffffff"));

        Button continueBtn = new Button("Vazhdo ➔");
        continueBtn.setFont(Font.font("Segoe UI", 22));
        continueBtn.setStyle("-fx-background-radius: 25; -fx-background-color: #f5f1e6; -fx-text-fill: #4e342e;");
        continueBtn.setPadding(new Insets(10, 30, 10, 30));
        continueBtn.setEffect(new DropShadow(10, Color.web("#d7ccc8")));

        continueBtn.setOnAction(e -> showMain(stage));

        welcomeBox.getChildren().addAll(title, subtitle, continueBtn);

        Scene welcomeScene = new Scene(welcomeBox, 1000, 600);
        stage.setScene(welcomeScene);
        stage.setTitle("Libraria Online");
        stage.show();
    }

    private void showMain(Stage stage) {
        try {
            socket = new Socket("localhost", 6000);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ex) {
            showAlert("Gabim", "Nuk u lidh me serverin!");
            Platform.exit();
            return;
        }

        Label headerIcon = new Label("📖");
        headerIcon.setFont(Font.font("Segoe UI Emoji", 38));
        Label headerTitle = new Label("Libraria Online");
        headerTitle.setFont(Font.font("Segoe UI", 32));
        headerTitle.setTextFill(Color.web("#4e342e"));

        HBox header = new HBox(15, headerIcon, headerTitle);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20, 0, 20, 30));
        header.setBackground(new Background(new BackgroundFill(Color.web("#efebe9"), CornerRadii.EMPTY, Insets.EMPTY)));

        searchField = new TextField();
        searchField.setPromptText("Kërko libër...");
        searchField.setPrefWidth(180);

        Button searchBtn = new Button("🔍");
        searchBtn.setFont(Font.font(18));
        searchBtn.setStyle("-fx-background-radius: 20; -fx-background-color: #d7ccc8; -fx-text-fill: #4e342e;");
        searchBtn.setOnAction(e -> searchBook());

        bookIdField = new TextField();
        bookIdField.setPromptText("ID librit");
        bookIdField.setPrefWidth(90);

        Button borrowBtn = new Button("📚 Huazo");
        borrowBtn.setStyle("-fx-background-radius: 20; -fx-background-color: #d7ccc8; -fx-text-fill: #4e342e;");
        borrowBtn.setOnAction(e -> borrowBook());

        Button returnBtn = new Button("↩️ Kthe");
        returnBtn.setStyle("-fx-background-radius: 20; -fx-background-color: #d7ccc8; -fx-text-fill: #4e342e;");
        returnBtn.setOnAction(e -> returnBook());

        Button listBtn = new Button("📋 Listo librat");
        listBtn.setStyle("-fx-background-radius: 20; -fx-background-color: #d7ccc8; -fx-text-fill: #4e342e;");
        listBtn.setOnAction(e -> listBooks());

        Button exitBtn = new Button("❌ Dil");
        exitBtn.setStyle("-fx-background-radius: 20; -fx-background-color: #bcaaa4; -fx-text-fill: white;");
        exitBtn.setOnAction(e -> exitClient());

        HBox commands = new HBox(10, searchField, searchBtn, bookIdField, borrowBtn, returnBtn, listBtn, exitBtn);
        commands.setPadding(new Insets(18, 30, 18, 30));
        commands.setAlignment(Pos.CENTER_LEFT);

        cardsPane = new FlowPane();
        cardsPane.setPadding(new Insets(20, 30, 20, 30));
        cardsPane.setHgap(20);
        cardsPane.setVgap(20);
        cardsPane.setPrefHeight(320);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefRowCount(5);
        outputArea.setStyle("-fx-background-radius: 18; -fx-font-family: 'Consolas'; -fx-font-size: 14;");

        VBox root = new VBox(header, commands, cardsPane, outputArea);
        VBox.setVgrow(cardsPane, Priority.ALWAYS);
        root.setSpacing(10);
        root.setBackground(new Background(new BackgroundFill(Color.web("#f5f1e6"), CornerRadii.EMPTY, Insets.EMPTY)));

        try {
            String welcome = input.readLine();
            outputArea.appendText(welcome + "\n");
        } catch (IOException ignored) {}

        listBooks();

        Scene scene = new Scene(root, 1000, 650);
        stage.setScene(scene);
        stage.setTitle("Libraria Online");
        stage.show();
    }

    private void sendCommand(String cmd) {
        output.println(cmd);
    }

    private String readResponse() throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        socket.setSoTimeout(500);
        try {
            while ((line = input.readLine()) != null && !line.isEmpty()) {
                sb.append(line).append("\n");
            }
        } catch (java.net.SocketTimeoutException e) {}
        socket.setSoTimeout(0);
        return sb.toString().trim();
    }

    private void searchBook() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            showAlert("Kujdes", "Shkruajni një fjalë kyçe për kërkimin.");
            return;
        }
        sendCommand("KERKO;" + keyword);
        try {
            String response = readResponse();
            outputArea.appendText("Rezultatet e kërkimit për '" + keyword + "':\n" + response + "\n");
            updateCardsFromResponse(response);
        } catch (IOException e) {
            outputArea.appendText("Gabim gjatë marrjes së përgjigjes.\n");
        }
    }

    private void borrowBook() {
        String id = bookIdField.getText().trim();
        if (id.isEmpty()) {
            CustomModal.show(getPrimaryStage(), "Kujdes", "Shkruajni ID-në e librit për huazim.");
            return;
        }
        sendCommand("HUAZO;" + id);
        try {
            String response = input.readLine();
            CustomModal.show(getPrimaryStage(), "Huazim libri", response);
            listBooks();
        } catch (IOException e) {
            outputArea.appendText("Gabim gjatë marrjes së përgjigjes.\n");
        }
    }

    private void returnBook() {
        String id = bookIdField.getText().trim();
        if (id.isEmpty()) {
            CustomModal.show(getPrimaryStage(), "Kujdes", "Shkruajni ID-në e librit për kthim.");
            return;
        }
        sendCommand("KTHE;" + id);
        try {
            String response = input.readLine();
            CustomModal.show(getPrimaryStage(), "Kthim libri", response);
            listBooks();
        } catch (IOException e) {
            outputArea.appendText("Gabim gjatë marrjes së përgjigjes.\n");
        }
    }

    private void listBooks() {
        sendCommand("LISTO");
        try {
            String response = readResponse();
//            outputArea.appendText("Lista e librave:\n" + response + "\n");
            updateCardsFromResponse(response);
        } catch (IOException e) {
//            outputArea.appendText("Gabim gjatë marrjes së listës.\n");
        }
    }

    private void updateCardsFromResponse(String response) {
        cardsPane.getChildren().clear();
        if (response == null || response.isEmpty()) return;
        String[] lines = response.split("\n");
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length < 4) continue;
            String id = parts[0].replace("ID:", "").trim();
            String title = parts[1].replace("Titulli:", "").trim();
            String author = parts[2].replace("Autori:", "").trim();
            String available = parts[3].replace("Në dispozicion:", "").trim();

            VBox card = new VBox(8);
            card.setPadding(new Insets(15));
            card.setPrefWidth(220);
            card.setStyle(
                    "-fx-background-color: #fffaf0; " +
                            "-fx-background-radius: 15; " +
                            "-fx-effect: dropshadow(gaussian, #a1887f55, 8, 0, 0, 2);"
            );

            Label titleLbl = new Label(title);
            titleLbl.setFont(Font.font("Segoe UI", 18));
            titleLbl.setTextFill(Color.web("#6d4c41"));
            Label authorLbl = new Label("Autori: " + author);
            Label idLbl = new Label("ID: " + id);
            Label availableLbl = new Label("Në dispozicion: " + available);

            card.getChildren().addAll(titleLbl, authorLbl, idLbl, availableLbl);

            cardsPane.getChildren().add(card);
        }
    }

    private void exitClient() {
        sendCommand("EXIT");
        try {
            String response = input.readLine();
            outputArea.appendText(response + "\n");
        } catch (IOException ignored) {}
        try {
            socket.close();
        } catch (IOException ignored) {}
        Platform.exit();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}