package com.example.notepad.Utility;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.notepad.Note;
import com.example.notepad.Activities.NoteActivity;
import com.example.notepad.R;

public class NotificationUtility {

    private static final String NOTIFICATION_CHANNEL_0 = "CHANNEL0";
    private static final int SHOW_NOTE = 0;

    public static void generatedNotification(Context context , Note note) {
        final PendingIntent pendingIntent = getPendingIntent(context, note.getId());
        final Notification notification = getNotificationBuilder(context, note, pendingIntent);
        NotificationManagerCompat.from(context).notify(note.getId(),notification);
    }

    private static Notification getNotificationBuilder(Context context, Note note, PendingIntent pendingIntent) {
        NotificationCompat.Style bigTextNotificationStyle = new NotificationCompat.BigTextStyle()
                .bigText(note.getNoteText())
                .setBigContentTitle("Note Remainder : " + note.getNoteText())
                .setSummaryText("Note's Summary");

        return new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_0)
                .setContentTitle(note.getNoteTitle())
                .setContentText(note.getNoteText())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(bigTextNotificationStyle)
                .setSmallIcon(R.drawable.ic_add_circle_outline_black_24dp)
                .build()
                ;
    }

    private static PendingIntent getPendingIntent(Context context,int noteId) {
        Intent intent = new Intent(context, NoteActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("NOTE_ID",noteId);
        return PendingIntent.getActivity(context, SHOW_NOTE,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
