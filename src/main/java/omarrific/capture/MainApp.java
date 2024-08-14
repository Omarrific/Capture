package omarrific.capture;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.File;

public class MainApp extends Application {

    private static final String CAPTURE_DIR = System.getProperty("user.home") + File.separator + "Capture";
    private static final String NOTES_DIR = CAPTURE_DIR + File.separator + "Notes";

    @Override
    public void start(Stage primaryStage) {
        try {
            createDirectories();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/omarrific/capture/MainView.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, 800, 600);
            primaryStage.setTitle("Capture");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createDirectories() {
        File captureDir = new File(CAPTURE_DIR);
        File notesDir = new File(NOTES_DIR);

        if (!captureDir.exists()) {
            captureDir.mkdir();
        }

        if (!notesDir.exists()) {
            notesDir.mkdir();
        }
    }

    public static String getNotesDirectory() {
        return NOTES_DIR;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
