package omarrific.capture;

import java.io.*;

public class NoteStorage {

    public static void saveNoteToFile(Note note, String filePath) throws IOException {
        try(FileOutputStream fileOut = new FileOutputStream(filePath);
            ObjectOutputStream out = new ObjectOutputStream(fileOut))
        {
            out.writeObject(note);
        }
    }

    public static Note loadNoteFromFile(String filePath) throws IOException,ClassNotFoundException{
        try(FileInputStream fileIn = new FileInputStream(filePath);
            ObjectInputStream in = new ObjectInputStream(fileIn))
        {
            return (Note) in.readObject();
        }
    }

}
