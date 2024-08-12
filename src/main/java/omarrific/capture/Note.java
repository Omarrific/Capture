package omarrific.capture;

import java.io.Serializable;

import java.time.LocalDateTime;

public class Note implements Serializable {
    private String title;
    private String content;
    private LocalDateTime creationDate;
    private LocalDateTime lastModifiedDate;

    public Note(String title, String content)
    {
        this.title = title;
        this.content = content;
        this.creationDate = LocalDateTime.now();
        this.lastModifiedDate = LocalDateTime.now();
    }

    public void updateContent(String content)
    {
        setContent(content);
        setLastModifiedDate(LocalDateTime.now());
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
