package omarrific.capture;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MainController {

    @FXML
    private VBox menuPane;
    @FXML
    private Button newNoteButton;
    @FXML
    private Label noNoteSelectedLabel;
    @FXML
    private TextField searchBar;
    @FXML
    private Button loginButton;
    @FXML
    private BorderPane borderPane;
    @FXML
    private StackPane contentArea;
    @FXML
    private Button closeMenuButton;
    @FXML
    private Button reopenMenuButton;
    @FXML
    private ListView<String> notesListView;

    private TextArea noteContentArea;
    private String selectedNoteFileName;

    @FXML
    private void initialize() {
        closeMenuButton.setOnAction(event -> closeMenu());
        reopenMenuButton.setOnAction(event -> openMenu());
        newNoteButton.setOnMousePressed(event -> createNote());

        noteContentArea = new TextArea();
        noteContentArea.setWrapText(true);
        noteContentArea.setEditable(false);
        noteContentArea.setOnKeyReleased(event -> handleNoteEditing());
        contentArea.getChildren().add(noteContentArea);

        updateNotesList();

        notesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedNoteFileName = newValue;
                if (noteExists(newValue)) {
                    loadNoteContent(newValue);
                    noNoteSelectedLabel.setVisible(false);
                } else {
                    clearNoteContent();
                    noNoteSelectedLabel.setVisible(true);
                }
            } else {
                clearNoteContent();
                noNoteSelectedLabel.setVisible(true);
            }
        });
    }

    private void createNote() {
        String noteId = UUID.randomUUID().toString();
        String fileName = "note_" + noteId + ".note";

        Note newNote = new Note("Untitled Note", "");

        String filePath = MainApp.getNotesDirectory() + File.separator + fileName;
        try {
            NoteStorage.saveNoteToFile(newNote, filePath);
            System.out.println("New note created and saved to: " + filePath);
            updateNotesList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateNotesList() {
        File notesDir = new File(MainApp.getNotesDirectory());

        File[] noteFiles = notesDir.listFiles((dir, name) -> name.endsWith(".note"));

        notesListView.getItems().clear();
        if (noteFiles != null) {
            List<File> sortedFiles = Arrays.stream(noteFiles)
                    .sorted(Comparator.comparingLong(File::lastModified).reversed())
                    .collect(Collectors.toList());

            for (File file : sortedFiles) {
                notesListView.getItems().add(file.getName());
            }
        }
    }

    private void loadNoteContent(String fileName) {
        String filePath = MainApp.getNotesDirectory() + File.separator + fileName;

        try {
            Note note = NoteStorage.loadNoteFromFile(filePath);
            noteContentArea.setText(note.getContent());
            noteContentArea.setEditable(true);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void clearNoteContent() {
        noteContentArea.clear();
        noteContentArea.setEditable(false);
    }

    private void handleNoteEditing() {
        if (selectedNoteFileName != null && noteExists(selectedNoteFileName)) {
            String filePath = MainApp.getNotesDirectory() + File.separator + selectedNoteFileName;

            try {
                Note note = NoteStorage.loadNoteFromFile(filePath);
                note.updateContent(noteContentArea.getText());
                NoteStorage.saveNoteToFile(note, filePath);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean noteExists(String fileName) {
        File file = new File(MainApp.getNotesDirectory() + File.separator + fileName);
        return file.exists();
    }

    private void closeMenu() {
        menuPane.setVisible(false);
        reopenMenuButton.setVisible(true);

        borderPane.setLeft(null);
        borderPane.setCenter(contentArea);
        contentArea.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    private void openMenu() {
        menuPane.setVisible(true);
        reopenMenuButton.setVisible(false);

        borderPane.setLeft(menuPane);
        borderPane.setCenter(contentArea);
    }
}
