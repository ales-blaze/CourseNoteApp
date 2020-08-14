package com.example.notepad.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.notepad.Note;
import com.example.notepad.NoteWithCourse;

import java.util.List;

@Dao
public interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(Note note);
    @Update
    void update(Note note);
    @Delete
    void delete(Note note);
    @Query("SELECT * FROM note WHERE note_id=:noteId")
    Note getNoteById(int noteId);
    @Query("SELECT * FROM note")
    LiveData<List<Note>> getAllNotes();
    @Transaction
    @Query("Select * FROM note")
    LiveData<List<NoteWithCourse>> getNotesWithCourse();
    @Query("SELECT * FROM note WHERE course_id=:courseId AND note_title=:noteTitle")
    Note getNoteWithTitleAndCourseId(int courseId, String noteTitle);
//    @Query("SELECT * FROM note WHERE note_title=:noteTitle AND course_id=:courseId")
}
