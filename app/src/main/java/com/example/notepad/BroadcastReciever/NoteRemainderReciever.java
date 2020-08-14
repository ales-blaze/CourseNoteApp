package com.example.notepad.BroadcastReciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.notepad.Note;
import com.example.notepad.Utility.NotificationUtility;
import com.google.gson.Gson;

public class NoteRemainderReciever extends BroadcastReceiver {

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        if (intent != null && context != null) {
            Note note = new Gson().fromJson(intent.getStringExtra("NOTE"),Note.class);
            if (note != null) {
                NotificationUtility.generatedNotification(context,note);
            }
        }
        return;
    }

    public static Intent getIntent(Context context,Note note) {
        Intent intent = new Intent(context,NoteRemainderReciever.class);
        intent.putExtra("NOTE", new Gson().toJson(note));
        return intent;
    }
}
