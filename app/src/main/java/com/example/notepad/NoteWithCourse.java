package com.example.notepad;

import androidx.room.Embedded;
import androidx.room.Relation;

public class NoteWithCourse {
    @Embedded
    private Note note;
    @Relation(
            parentColumn = "course_id",
            entityColumn = "course_id"
    )
    private Course course;

    public Course getCourse() {
        return course;
    }

    public Note getNote() {
        return note;
    }
}
