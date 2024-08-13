package omarrific.capture;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainController {

    @FXML
    private BorderPane borderPane;

    @FXML
    private VBox menuPane;

    @FXML
    private StackPane contentArea;

    @FXML
    private Button closeMenuButton;

    @FXML
    private Button reopenMenuButton;

    @FXML
    private void initialize() {
        closeMenuButton.setOnAction(event -> closeMenu());
        reopenMenuButton.setOnAction(event -> openMenu());
    }

    private void closeMenu() {
        menuPane.setVisible(false);
        reopenMenuButton.setVisible(true);

        // Center content area and make it fullscreen
        borderPane.setLeft(null); // Hide the menu
        borderPane.setCenter(contentArea); // Keep the note contents centered
        contentArea.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    private void openMenu() {
        menuPane.setVisible(true);
        reopenMenuButton.setVisible(false);

        // Restore layout
        borderPane.setLeft(menuPane);
        borderPane.setCenter(contentArea); // Keep content visible even when menu is open
    }
}
