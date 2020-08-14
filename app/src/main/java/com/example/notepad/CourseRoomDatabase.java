package com.example.notepad;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.notepad.Dao.CourseDao;
import com.example.notepad.Dao.NoteDao;

@Database(entities = {Course.class, Note.class},version = 2,exportSchema = false)
public abstract class CourseRoomDatabase extends RoomDatabase {
    public abstract CourseDao getCourseDao();
    public abstract NoteDao getNoteDao();
    private static CourseRoomDatabase INSTANCE;

    public static CourseRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CourseRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context,CourseRoomDatabase.class,"Notepad")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
