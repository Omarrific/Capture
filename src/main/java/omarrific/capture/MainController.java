package omarrific.capture;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.IOException;

public class MainController {

    @FXML
    private TextArea noteArea;

    @FXML
    private Button newNote;

    @FXML
    private Button saveNote;

    @FXML
    private Button loadNote;


    @FXML
    private Button deleteNote;

    private static final String NOTES_DIR = "notes";
    @FXML
    private void initialize()
    {
        File notesDir = new File(NOTES_DIR);

        if(!notesDir.exists())
        {
            notesDir.mkdir();
        }
        newNote.setOnAction(e -> newNoteHandler());
        saveNote.setOnAction(e -> saveNoteHandler(noteArea.getText()));
        loadNote.setOnAction(e -> loadNoteHandler());
        deleteNote.setOnAction(e-> deleteNoteHandler());

    }

    private void newNoteHandler()
    {
        System.out.println("hi");
        noteArea.clear();
    }

    private void saveNoteHandler(String content)
    {
        System.out.println("hi");
        if(content.isEmpty())
        {
            //TBD, show an alert possibly?
            return;
        }

        String title = content.split("\n", 2)[0];
        String fileName = title.replaceAll("\\s+", "_") + ".note"; // Replace spaces in title with underscores
        Path filePath = Paths.get(NOTES_DIR, fileName);

        Note note = new Note(title, content);

        try{
            NoteStorage.saveNoteToFile(note,filePath.toString());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private void deleteNoteHandler()
    {
        System.out.println("hi");
        noteArea.clear();
    }

    //handling loading notes later
    private void loadNoteHandler()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Note");

        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Note files (*.note)", "*.note");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File notesDirectory = new File("notes");
        fileChooser.setInitialDirectory(notesDirectory);

        File file = fileChooser.showOpenDialog(new Stage());



        if(file != null)
        {
            try{
                Note note = NoteStorage.loadNoteFromFile(file.getAbsolutePath());
                noteArea.setText(note.getContent());
            }
            catch(IOException | ClassNotFoundException e){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error Loading Note");
                alert.setContentText("An error occurred while loading the note: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

}