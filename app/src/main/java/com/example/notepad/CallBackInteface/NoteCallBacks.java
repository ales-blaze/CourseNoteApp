package com.example.notepad.CallBackInteface;

import com.example.notepad.Note;

import java.util.List;
import java.util.Map;

public interface NoteCallBacks {
    void noteUploadFailed(Exception e);
    void errorFetchingNotes();
    void addNoteToLocalDatabase(List<Note> notes, Map<Integer,Integer> courseIdMappings);
    void noteUploadSucceeded(String referenceId);
}
