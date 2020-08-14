package com.example.notepad.Repository;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.notepad.CallBackInteface.NoteCallBacks;
import com.example.notepad.CourseRoomDatabase;
import com.example.notepad.Dao.NoteDao;
import com.example.notepad.Note;
import com.example.notepad.NoteWithCourse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class NoteRepository {
    private LiveData<List<Note>> notes;
    private LiveData<List<NoteWithCourse>> noteWithCourse;
    private NoteDao noteDao;
    private static final String TAG = "NoteRepository";

    public NoteRepository(Application application) {
        noteDao = CourseRoomDatabase.getDatabase(application).getNoteDao();
        notes = noteDao.getAllNotes();
        noteWithCourse = noteDao.getNotesWithCourse();
    }

    public LiveData<List<NoteWithCourse>> getNoteWithCourse() {
        return noteWithCourse;
    }

    public LiveData<List<Note>> getAllNotes() {
        return notes;
    }

    public void insert(Note note) {
        try {
            new insertTask(noteDao).execute(note).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class insertTask extends AsyncTask<Note,Void,Void> {
        private final NoteDao noteDao;

        insertTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }
        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.insert(notes[0]);
            return null;
        }
    }

    public void update(Note note) {
        try {
            new updateTask(noteDao).execute(note).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class updateTask extends AsyncTask<Note,Void,Void> {
        private final NoteDao noteDao;
        updateTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }
        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.update(notes[0]);
            return null;
        }
    }

    public void delete(Note note) {
        try {
            new deleteTask(noteDao).execute(note).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class deleteTask extends AsyncTask<Note,Void,Void> {
        private final NoteDao noteDao;
        public deleteTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }
        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.delete(notes[0]);
            return null;
        }
    }

    public Note getNoteById(int noteId) {
        try {
            return new getNoteByIdTask(noteDao).execute(noteId).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class getNoteByIdTask extends AsyncTask<Integer,Void,Note> {
        private final NoteDao noteDao;
        public getNoteByIdTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }
        @Override
        protected Note doInBackground(Integer... ids) {
            return noteDao.getNoteById(ids[0]);
        }
    }

    public Note getNoteWithTitleAndCourseId(int courseId, String noteTitle) {
        try {
            return new getNoteWithTitleAndCourseIdTask(noteDao).execute(new String[]{String.valueOf(courseId),noteTitle}).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Note();
    }

    private static class getNoteWithTitleAndCourseIdTask extends AsyncTask<String,Void,Note> {
        private final NoteDao noteDao;
        public getNoteWithTitleAndCourseIdTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }
        @Override
        protected Note doInBackground(String... strings) {
            if (strings.length < 2) return new Note();
            try {
                return noteDao.getNoteWithTitleAndCourseId(Integer.parseInt(strings[0]), strings[1]);
            }catch (NumberFormatException numException) {
                Log.e(TAG, "doInBackground: Invalid course Id format");
            }
            return new Note();
        }
    }

    public void addNoteToCloud(Note note , final NoteCallBacks noteCallbacks) {
        if (note == null || noteCallbacks == null) {
            Log.e(TAG, "addNoteToCloud: Parameter must not be null", new IllegalArgumentException());
            return;
        }
        FirebaseFirestore.getInstance()
                .collection("notes")
                .add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        noteCallbacks.noteUploadSucceeded(documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        noteCallbacks.noteUploadFailed(e);
                    }
                });
    }

    public void getNotesFromCloud(final NoteCallBacks noteCallBacks, final Map<Integer,Integer> courseIdMappings) {
        if (noteCallBacks == null) {
            Log.e(TAG, "getNotesFromCloud: No Callback Provided", new IllegalArgumentException());
            return;
        }
        final List<Note> backupNotes = new ArrayList<>();
        FirebaseFirestore.getInstance()
                .collection("notes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Note note = document.toObject(Note.class);
                                backupNotes.add(note);
                            }
                            noteCallBacks.addNoteToLocalDatabase(backupNotes,courseIdMappings);
                        } else {
                            noteCallBacks.errorFetchingNotes();
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}

