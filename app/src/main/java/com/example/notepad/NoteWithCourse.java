package com.example.notepad;

import androidx.room.Embedded;
import androidx.room.Relation;

public class NoteWithCourse {
    @Embedded
    public Note note;
    @Relation(
            parentColumn = "course_id",
            entityColumn = "course_id"
    )
    public Course course;
}
