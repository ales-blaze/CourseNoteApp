package com.example.notepad.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.notepad.CallBackInteface.NoteCallBacks;
import com.example.notepad.Note;
import com.example.notepad.NoteWithCourse;
import com.example.notepad.Repository.NoteRepository;

import java.util.List;
import java.util.Map;

public class NoteViewModel extends AndroidViewModel {
    public static final int INVALID_ID = -1;
    private NoteRepository noteRepository;
    private LiveData<List<Note>> notes;
    private  LiveData<List<NoteWithCourse>> noteWithCourse;

    private static final String TAG = "NoteViewModel";

    public NoteViewModel(@NonNull Application application) {
        super(application);
        noteRepository = new NoteRepository(application);
        notes = noteRepository.getAllNotes();
        noteWithCourse = noteRepository.getNoteWithCourse();
    }

    public LiveData<List<NoteWithCourse>> getNoteWithCourse() {
        return noteWithCourse;
    }

    public LiveData<List<Note>> getAllNotes() {
        return notes;
    }

    public void insert(@NonNull Note note) {
        noteRepository.insert(note);
    }

    public void update(@NonNull Note note) {
        noteRepository.update(note);
    }

    public void delete(@NonNull Note note) {
        noteRepository.delete(note);
    }

    public Note getNoteById(int noteId) {
        Note note = noteRepository.getNoteById(noteId);
        if (note == null) {
            Log.e("NoteViewModel ", "getNoteById: " + "Note with " + noteId + " doesn't exists");
        }else {
            return note;
        }
        return null;
    }

    public Note getNoteWithTitleAndCourseId(@NonNull int courseId, @NonNull String noteTitle) {
        if (noteTitle == null) {
            Log.e(TAG, "getNoteWithTitleAndCourseId: Note Title is Null"  );
            return new Note();
        }
        if (courseId < 0) {
            Log.e(TAG, "getNoteWithTitleAndCourseId: Invalid Course Id");
            return new Note();
        }
        return noteRepository.getNoteWithTitleAndCourseId(courseId,noteTitle);
    }

    public void getNotesFromCloud(NoteCallBacks noteCallBacks , Map<Integer,Integer> courseIdMappings) {
        noteRepository.getNotesFromCloud(noteCallBacks,courseIdMappings);
    }

    public void addNoteToCloud(Note note , NoteCallBacks noteCallBacks) {
        noteRepository.addNoteToCloud(note,noteCallBacks);
    }
}
