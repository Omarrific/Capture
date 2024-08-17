package omarrific.capture;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MainController {

    @FXML
    public Button saveButton;
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
    private ToggleButton modeToggleButton;
    private Scene scene;

    @FXML
    private void initialize() {
        closeMenuButton.setOnAction(event -> closeMenu());
        reopenMenuButton.setOnAction(event -> openMenu());
        newNoteButton.setOnMousePressed(event -> createNote());


        noteContentArea = new TextArea();
        noteContentArea.setWrapText(true);
        noteContentArea.setEditable(false);
        noteContentArea.setText("No note selected");
        contentArea.getChildren().add(noteContentArea);
        contentArea.setStyle("-fx-background:blue");

        updateNotesList();

        notesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedNoteFileName = newValue + ".note";
                if (noteExists(selectedNoteFileName)) {
                    loadNoteContent(selectedNoteFileName);
                } else {
                    clearNoteContent();
                }
            } else {
                clearNoteContent();
            }
        });

        notesListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedNote = notesListView.getSelectionModel().getSelectedItem();
                if (selectedNote != null) {
                    renameNote(selectedNote);
                }
            }
        });


        //dark theme light theme
        modeToggleButton.setOnAction(event -> toggleMode());


        //toolbar stuff


    }


    private void createNote() {
        String noteName;
        while (true) {
            TextInputDialog dialog = new TextInputDialog("Untitled Note");
            dialog.setTitle("New Note");
            dialog.setHeaderText("Enter the name for the new note:");
            dialog.setContentText("Note Name:");

            noteName = dialog.showAndWait().orElse(null);

            if (noteName == null) {
                return;
            }

            noteName = noteName.trim();

            if (noteName.isEmpty()) {
                showAlert("Note Name Required", "You must enter a name for the note.");
                continue;
            }

            File noteFile = new File(MainApp.getNotesDirectory() + File.separator + noteName + ".note");
            if (noteFile.exists()) {
                showAlert("Name Exists", "A note with this name already exists. Please choose a different name.");
            } else {
                break;
            }
        }

        String fileName = noteName + ".note";
        Note newNote = new Note(noteName, "");
        String filePath = MainApp.getNotesDirectory() + File.separator + fileName;
        try {
            NoteStorage.saveNoteToFile(newNote, filePath);
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
                String fileNameWithoutExtension = file.getName().replace(".note", "");
                notesListView.getItems().add(fileNameWithoutExtension);
            }
        }

        notesListView.setCellFactory(listView -> new ListCell<>() {
            private final Button deleteButton = new Button();
            private final ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/deleteFile.png")));

            {
                deleteIcon.setFitWidth(16);
                deleteIcon.setFitHeight(16);
                deleteButton.setGraphic(deleteIcon);
                deleteButton.setOnAction(event -> {
                    String noteName = getItem();
                    if (noteName != null) {
                        deleteNote(noteName);
                    }
                });

                ContextMenu contextMenu = new ContextMenu();
                MenuItem openItem = new MenuItem("Open");
                MenuItem renameItem = new MenuItem("Rename");
                MenuItem deleteItem = new MenuItem("Delete");

                openItem.setOnAction(event -> {
                    String noteName = getItem();
                    if (noteName != null) {
                        openNote(noteName);
                    }
                });

                renameItem.setOnAction(event -> {
                    String noteName = getItem();
                    if (noteName != null) {
                        renameNote(noteName);
                    }
                });

                deleteItem.setOnAction(event -> {
                    String noteName = getItem();
                    if (noteName != null) {
                        deleteNote(noteName);
                    }
                });

                contextMenu.getItems().addAll(openItem, renameItem, deleteItem);

                setOnContextMenuRequested(event -> contextMenu.show(this, event.getScreenX(), event.getScreenY()));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setGraphic(deleteButton);
                }
            }
        });
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
        noteContentArea.setText("No note selected");
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

    private void deleteNote(String noteName) {
        String filePath = MainApp.getNotesDirectory() + File.separator + noteName + ".note";
        File file = new File(filePath);
        if (file.delete()) {
            updateNotesList();
            clearNoteContent();
        } else {
            showAlert("Delete Failed", "Failed to delete the note.");
        }
    }

    private void openNote(String noteName) {
        selectedNoteFileName = noteName + ".note";
        if (noteExists(selectedNoteFileName)) {
            loadNoteContent(selectedNoteFileName);
        }
    }

    private void renameNote(String noteName) {
        TextInputDialog dialog = new TextInputDialog(noteName);
        dialog.setTitle("Rename Note");
        dialog.setHeaderText("Enter the new name for the note:");
        dialog.setContentText("New Note Name:");

        String newNoteName = dialog.showAndWait().orElse(noteName).trim();
        if (newNoteName.isEmpty()) {
            showAlert("Note Name Required", "You must enter a new name for the note.");
            return;
        }

        File oldFile = new File(MainApp.getNotesDirectory() + File.separator + noteName + ".note");
        File newFile = new File(MainApp.getNotesDirectory() + File.separator + newNoteName + ".note");
        if (newFile.exists()) {
            showAlert("Name Exists", "A note with this name already exists. Please choose a different name.");
            return;
        }

        if (oldFile.renameTo(newFile)) {
            updateNotesList();
            if (noteName.equals(selectedNoteFileName)) {
                selectedNoteFileName = newNoteName + ".note";
                loadNoteContent(selectedNoteFileName);
            }
        } else {
            showAlert("Rename Failed", "Failed to rename the note.");
        }
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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setScene(Scene scene){
        this.scene = scene;
    }

    private void toggleMode() {
        if (modeToggleButton.isSelected()) {
            scene.getStylesheets().remove(getClass().getResource("/omarrific/capture/styling.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/omarrific/capture/darkTheme.css").toExternalForm());
            modeToggleButton.setText("Light Mode");
        } else {
            scene.getStylesheets().add(getClass().getResource("/omarrific/capture/styling.css").toExternalForm());
            scene.getStylesheets().remove(getClass().getResource("/omarrific/capture/darkTheme.css").toExternalForm());
            modeToggleButton.setText("Dark Mode");
        }
    }



}
