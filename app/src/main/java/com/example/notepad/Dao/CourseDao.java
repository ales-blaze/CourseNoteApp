package com.example.notepad.Dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.notepad.Course;

import java.util.List;
@Dao
public interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(Course course);
    @Update
    void update(Course course);
    @Delete
    void delete(Course course);
    @Query("SELECT * FROM course")
    LiveData<List<Course>> getAllCourses();
    @Query("SELECT * FROM course WHERE course_id=:courseId")
    Course getCourse(int courseId);
    @Query("SELECT * FROM course WHERE title=:courseTitle")
    int getCourseIdByTitle(String courseTitle);
    @Query("DELETE FROM note WHERE note.course_id = :courseId")
    void deleteNoteWithCourseId(@NonNull int courseId);
    @Query("SELECT * FROM course WHERE title=:title")
    Course getCourseWithGivenTitle(String title);
}
