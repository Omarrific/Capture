package omarrific.capture;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {


        primaryStage.setTitle("Capture");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        BorderPane layout = loader.load();

        Scene scene = new Scene(layout, 800,600);
        primaryStage.setScene(scene);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch();
    }
}