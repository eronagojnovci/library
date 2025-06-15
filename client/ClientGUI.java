package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class ClientGUI extends Application {

    private TilePane cardsPane;
    private TextField searchField;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private Stage primaryStage;
    private Label headerTitle;
    private Label headerIcon;

    private boolean isDarkMode = false;
    private VBox root;
    private List<Button> allButtons = new ArrayList<>();
    private ToggleButton darkModeToggle;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showWelcome(primaryStage);
    }

    private void showWelcome(Stage stage) {
        VBox welcomeBox = new VBox(20);
        welcomeBox.setAlignment(Pos.CENTER);
        welcomeBox.setPadding(new Insets(150, 40, 60, 40));
        BackgroundImage bgImage = new BackgroundImage(
                new Image(getClass().getResource("/assets/lb.jpg").toExternalForm()),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(100, 100, true, true, false, true)
        );
        welcomeBox.setBackground(new Background(bgImage));

        Label title = new Label("Welcome to our Library!");
        title.setFont(Font.font("Segoe UI", 38));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Explore, borrow and enjoy your favorite books.");
        subtitle.setFont(Font.font("Segoe UI", 20));
        subtitle.setTextFill(Color.WHITE);

        Button continueBtn = new Button("Vazhdo ➔");
        continueBtn.setFont(Font.font("Segoe UI", 22));
        continueBtn.setStyle("-fx-background-radius: 25; -fx-background-color: #a1866f; -fx-text-fill: white;");
        continueBtn.setPadding(new Insets(10, 30, 10, 30));
        addHoverEffect(continueBtn);
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
            CustomModal.show(primaryStage, "Gabim", "Nuk u lidh me serverin!");
            System.out.println("Serveri duhet te jete ne funksion");
            Platform.exit();
            return;
        }

        headerIcon = new Label("📖");
        headerTitle = new Label("Libraria Online");

        headerIcon.setFont(Font.font("Segoe UI Emoji", 38));
        headerTitle.setFont(Font.font("Segoe UI", 32));
        headerTitle.setTextFill(Color.WHITE);

        HBox header = new HBox(15, headerIcon, headerTitle);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20, 0, 20, 30));
        header.setBackground(new Background(new BackgroundFill(Color.web("#a1866f"), CornerRadii.EMPTY, Insets.EMPTY)));

        darkModeToggle = new ToggleButton();
        darkModeToggle.setGraphic(new ImageView(new Image(getClass().getResource("/assets/moon.png").toExternalForm(), 20, 20, true, true)));
        darkModeToggle.setStyle("-fx-background-radius: 30;");
        darkModeToggle.setOnAction(e -> toggleDarkMode());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topBar = new HBox(10, header, spacer, darkModeToggle);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 30, 0, 30));
        topBar.setBackground(new Background(new BackgroundFill(Color.web("#a1866f"), CornerRadii.EMPTY, Insets.EMPTY)));

        searchField = new TextField();
        searchField.setPromptText("Kërko libër...");
        searchField.setPrefWidth(180);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                listBooks();
            }
        });


        Button searchBtn = createButton("🔍", "#d7b899");
        searchBtn.setOnAction(e -> searchBook());


        Button exitBtn = createButton("❌ Dil", "#c0392b");
        exitBtn.setOnAction(e -> exitClient());

        HBox commands = new HBox(10, searchField, searchBtn, exitBtn);
        commands.setPadding(new Insets(18, 30, 18, 30));
        commands.setAlignment(Pos.CENTER_LEFT);

        cardsPane = new TilePane();
        cardsPane.setPadding(new Insets(20, 60, 20, 60));
        cardsPane.setHgap(20);
        cardsPane.setVgap(20);
        cardsPane.setPrefColumns(4);
        cardsPane.setTileAlignment(Pos.TOP_CENTER);
        cardsPane.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(cardsPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        root = new VBox(topBar, commands, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        root.setSpacing(10);

        updateStyles();

        listBooks();


        Scene scene = new Scene(root, 1000, 650);
        stage.setScene(scene);
        stage.setTitle("Libraria Online");
        stage.show();
    }

    private Button createButton(String text, String bgColor) {
        Button btn = new Button(text);
        btn.setFont(Font.font(14));
        btn.setStyle("-fx-background-radius: 20; -fx-background-color: " + bgColor + "; -fx-text-fill: white;");
        addHoverEffect(btn);
        allButtons.add(btn);
        return btn;
    }

    private void addHoverEffect(Region node) {
        node.setOnMouseEntered(e -> node.setEffect(new DropShadow(12, Color.GRAY)));
        node.setOnMouseExited(e -> node.setEffect(null));
    }

    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        updateStyles();

        String iconPath = isDarkMode ? "/assets/sun.png" : "/assets/moon.png";
        darkModeToggle.setGraphic(new ImageView(new Image(getClass().getResource(iconPath).toExternalForm(), 20, 20, true, true)));
    }

    private void updateStyles() {
        if (root == null) return;

        String bgColor = isDarkMode ? "#3e2c1c" : "#f5f1e6";
        String textColor = isDarkMode ? "#f5f5f5" : "#333";

        root.setBackground(new Background(new BackgroundFill(Color.web(bgColor), CornerRadii.EMPTY, Insets.EMPTY)));
        if (headerTitle != null) headerTitle.setTextFill(Color.web(textColor));
        if (headerIcon != null) headerIcon.setTextFill(Color.web(textColor));

        for (Button btn : allButtons) {
            btn.setTextFill(Color.web("white"));
        }

        for (var node : cardsPane.getChildren()) {
            if (node instanceof VBox card) {
                if (isDarkMode) {
                    card.setStyle("-fx-background-color: #5a4633; -fx-border-color: #a1866f; -fx-border-width: 1; -fx-border-radius: 15; -fx-background-radius: 15;");
                } else {
                    card.setStyle("-fx-background-color: #fffaf4; -fx-border-color: #e0d6c3; -fx-border-width: 1; -fx-border-radius: 15; -fx-background-radius: 15;");
                }
                for (javafx.scene.Node child : card.getChildren()) {
                    if (child instanceof Label label) {
                        label.setTextFill(Color.web(textColor));
                    }
                }
            }
        }
    }

    // ...existing code...
    private void updateCardsFromResponse(String response) {
        cardsPane.getChildren().clear();
        if (response == null || response.isEmpty()) {
            Label notFound = new Label("Libri nuk u gjet.");
            notFound.setFont(Font.font("Segoe UI", 18));
            notFound.setTextFill(Color.web("#a1866f"));
            cardsPane.getChildren().add(notFound);
            return;
        }

        String[] lines = response.split("\n");
        boolean foundBook = false;

        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length < 5) continue;

            foundBook = true;
            // ...existing code for creating cards...
            // (copy the rest of your card creation code here)
            String id = parts[0].replace("ID:", "").trim();
            String title = parts[1].replace("Titulli:", "").trim();
            String author = parts[2].replace("Autori:", "").trim();
            String availableStr = parts[3].replace("Në dispozicion:", "").trim();
            String photoPath = parts[4].replace("Foto:", "").trim();

            int availableCount;
            try {
                availableCount = Integer.parseInt(availableStr);
            } catch (NumberFormatException e) {
                availableCount = 0;
            }

            VBox card = new VBox(10);
            card.setAlignment(Pos.TOP_CENTER);
            card.setPadding(new Insets(15));
            card.setPrefWidth(200);
            card.setStyle(
                    "-fx-background-color: #fffaf4;" +
                            "-fx-border-color: #e0d6c3;" +
                            "-fx-border-width: 1;" +
                            "-fx-border-radius: 15;" +
                            "-fx-background-radius: 15;"
            );

            ImageView imageView;
            try {
                imageView = new ImageView(new Image(getClass().getResource(photoPath).toExternalForm()));
            } catch (Exception e) {
                imageView = new ImageView(new Image(getClass().getResource("/assets/default.jpg").toExternalForm()));
            }
            imageView.setFitHeight(120);
            imageView.setFitWidth(100);
            imageView.setPreserveRatio(true);

            Label titleLabel = new Label(title);
            titleLabel.setFont(Font.font("Segoe UI", 16));
            titleLabel.setWrapText(true);
            titleLabel.setStyle("-fx-font-weight: bold;");
            titleLabel.setTextFill(Color.web("#5a4a3c"));

            Label authorLabel = new Label("Autori: " + author);
            authorLabel.setFont(Font.font("Segoe UI", 13));
            authorLabel.setTextFill(Color.web("#5a4a3c"));

            Label availLabel = new Label("Në dispozicion: " + availableCount);
            availLabel.setFont(Font.font("Segoe UI", 13));
            availLabel.setTextFill(Color.web("#5a4a3c"));

            Button actionBtn = new Button();
            actionBtn.setFont(Font.font(13));
            actionBtn.setStyle("-fx-background-radius: 20; -fx-padding: 5 15 5 15;");

            if (availableCount > 0) {
                actionBtn.setText("📚 Huazo");
                actionBtn.setStyle(actionBtn.getStyle() + "-fx-background-color: #bfa074; -fx-text-fill: white;");
            } else {
                actionBtn.setText("↩️ Kthe");
                actionBtn.setStyle(actionBtn.getStyle() + "-fx-background-color: #a1866f; -fx-text-fill: white;");
            }

            final int[] currentAvailable = {availableCount};

            actionBtn.setOnAction(e -> {
                if (actionBtn.getText().equals("📚 Huazo")) {
                    sendCommand("HUAZO;" + id);
                    try {
                        String resp = input.readLine();
                        CustomModal.show((Stage) cardsPane.getScene().getWindow(), "Huazim", resp);

                        currentAvailable[0]--;
                        availLabel.setText("Në dispozicion: " + currentAvailable[0]);

                        actionBtn.setText("↩️ Kthe");
                        actionBtn.setStyle("-fx-background-radius: 20; -fx-padding: 5 15 5 15; -fx-background-color: #a1866f; -fx-text-fill: white;");

                    } catch (IOException ex) {
                        CustomModal.show((Stage) cardsPane.getScene().getWindow(), "Gabim", "Gabim gjatë huazimit.");
                    }

                } else {
                    sendCommand("KTHE;" + id);
                    try {
                        String resp = input.readLine();
                        CustomModal.show((Stage) cardsPane.getScene().getWindow(), "Kthim", resp);

                        currentAvailable[0]++;
                        availLabel.setText("Në dispozicion: " + currentAvailable[0]);

                        actionBtn.setText("📚 Huazo");
                        actionBtn.setStyle("-fx-background-radius: 20; -fx-padding: 5 15 5 15; -fx-background-color: #bfa074; -fx-text-fill: white;");

                    } catch (IOException ex) {
                        CustomModal.show((Stage) cardsPane.getScene().getWindow(), "Gabim", "Gabim gjatë kthimit.");
                    }
                }
            });

            card.getChildren().addAll(imageView, titleLabel, authorLabel, availLabel, actionBtn);
            cardsPane.getChildren().add(card);
        }

        if (!foundBook) {
            Label notFound = new Label("Libri nuk u gjet.");
            notFound.setFont(Font.font("Segoe UI", 18));
            notFound.setTextFill(Color.web("#a1866f"));
            cardsPane.getChildren().add(notFound);
        }
        updateStyles();
    }
    // ...existing code...
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
        } catch (java.net.SocketTimeoutException ignored) {}
        socket.setSoTimeout(0);
        return sb.toString().trim();
    }

    private void searchBook() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            CustomModal.show(primaryStage, "Kujdes", "Shkruajni një fjalë kyçe për kërkim.");
            return;
        }
        sendCommand("KERKO;" + keyword);
        try {
            String response = readResponse();
            updateCardsFromResponse(response);
        } catch (IOException e) {
            CustomModal.show(primaryStage, "Gabim", "Gabim gjatë kërkimit.");
        }
    }

    private void listBooks() {
        sendCommand("LISTO");
        try {
            String response = readResponse();
            updateCardsFromResponse(response);
        } catch (IOException e) {
            CustomModal.show(primaryStage, "Gabim", "Gabim gjatë listimit.");
        }
    }

    private void exitClient() {
        sendCommand("DIL");
        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException ignored) {}
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);

    }
}