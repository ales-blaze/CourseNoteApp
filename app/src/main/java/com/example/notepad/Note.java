package com.example.notepad;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Note extends BaseObservable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "note_id")
    private int id;
    @ColumnInfo(name = "note_title")
    private String noteTitle;
    @ColumnInfo(name = "note_text")
    private String noteText;
    @ColumnInfo(name = "course_id")
    private int courseId;

    @Ignore
    private boolean isNew = false;
    @Ignore
    private boolean isDuplicate = false;


    public boolean isDuplicate() {
        return isDuplicate;
    }

    public void setDuplicate(boolean duplicate) {
        isDuplicate = duplicate;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    @Bindable
    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
        notifyPropertyChanged(BR.noteTitle);
    }
    @Bindable
    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
        notifyPropertyChanged(BR.noteText);
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Note note = (Note) o;

        if (id != note.id) return false;
        if (courseId != note.courseId) return false;
        if (noteTitle != null ? !noteTitle.equals(note.noteTitle) : note.noteTitle != null)
            return false;
        return noteText != null ? noteText.equals(note.noteText) : note.noteText == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (noteTitle != null ? noteTitle.hashCode() : 0);
        result = 31 * result + (noteText != null ? noteText.hashCode() : 0);
        result = 31 * result + courseId;
        return result;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", noteTitle='" + noteTitle + '\'' +
                ", noteText='" + noteText + '\'' +
                ", courseId=" + courseId +
                '}';
    }

    public boolean isEmpty() {
        return noteTitle.equals("") && noteText.equals("") && !isNew && courseId == 0 && id == 0;
    }
}
