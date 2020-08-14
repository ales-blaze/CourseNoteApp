package com.example.notepad.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.notepad.BroadcastReciever.NoteRemainderReciever;
import com.example.notepad.CallBackInteface.CourseCallbacks;
import com.example.notepad.CallBackInteface.NoteCallBacks;
import com.example.notepad.Course;
import com.example.notepad.Note;
import com.example.notepad.R;
import com.example.notepad.UserAccount;
import com.example.notepad.ViewModel.CourseViewModel;
import com.example.notepad.ViewModel.NoteViewModel;
import com.example.notepad.databinding.ActivityNoteBinding;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

public class NoteActivity extends AppCompatActivity implements CourseCallbacks, NoteCallBacks {
    public static final int NOTE_REMAINDER_BROADCAST = 0;
    public static final String NOTE = "NOTE";
    private NoteViewModel noteViewModel;
    private Note note;
    private ActivityNoteBinding noteBinding;
    private CourseViewModel courseViewModel;
    private int note_id;
    private Note duplicatedNote;
    private List<Integer> courseIds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        note = new Note();

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        courseViewModel = ViewModelProviders.of(this).get(CourseViewModel.class);
        noteBinding = DataBindingUtil.setContentView(this,R.layout.activity_note);

        if (getIntent() != null) {
            note_id = getIntent().getIntExtra("NOTE_ID",-1);
            if (note_id != -1) {
                note = noteViewModel.getNoteById(note_id);
            }else {
                note.setCourseId(0);
                note.setNoteText("");
                note.setNoteTitle("");
                note.setNew(true);
            }
        }else {
            return;
        }
        noteBinding.setNote(note);
        courseViewModel.getCourses().observe(this, new Observer<List<Course>>() {
            @Override
            public void onChanged(List<Course> courses) {
                List<String> courseName = new ArrayList<>();
                courseIds = new ArrayList<>();
                int id = 1 , i = 0;
                for (Course course : courses) {
                    if (course != null && course.getTitle() != null) {
                        if (note.getCourseId() == course.getId()) {
                            id = i;
                        }
                        courseName.add(course.getTitle());
                        courseIds.add(course.getId());
                    }
                    i++;
                }
                ArrayAdapter<String> courseList =
                        new ArrayAdapter<>
                                (NoteActivity.this, android.R.layout.simple_list_item_1, courseName);
                courseList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                noteBinding.spinner.setAdapter(courseList);
                noteBinding.spinner.setSelection(id);
            }
        });

        noteBinding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (courseIds != null && courseIds.size() > position) {
                    note.setCourseId(courseIds.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String noteString = savedInstanceState.getString(NOTE);
        Gson gson = new Gson();
        note = gson.fromJson(noteString,Note.class);
        noteBinding.setNote(note);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Gson gson = new Gson();
        outState.putString("NOTE",gson.toJson(note));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notes_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int itemId = item.getItemId();
        if(!preProcessBeforeAction()) {
            return false;
        }
        getNoteUiUpdate();
        if (itemId == R.id.save_note_menu) {
            isNoteDuplicate(note);
            if (note.isNew() && !note.isDuplicate()) {
                noteViewModel.insert(note);
            }else {
                if (note.isDuplicate()) {
                    duplicatedNote.setNoteText(duplicatedNote.getNoteText() + " \n " + note.getNoteText());
                    note = duplicatedNote;
                    Toast.makeText(this, "Note Merged", Toast.LENGTH_SHORT).show();
                }
                noteViewModel.update(note);
                if (!note.isDuplicate()) {
                    Toast.makeText(this, "NoteUpdate", Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }else if(itemId == R.id.delete_note_menu) {
            if (!note.isNew()) {
                removeAssociatedReminder(note);
                noteViewModel.delete(note);
            }
            finish();
        }else if(itemId == R.id.remainder_note_menu) {
            setRemainder(note);
        }else if(itemId == R.id.mail_note_menu) {
            sendMail(note);
        }else if(itemId == R.id.backup_note_menu) {
            backupNote(note);
        }
        return super.onOptionsItemSelected(item);
    }

    private void backupNote(@NonNull final Note note1) {
        if (UserAccount.getInstance(this).isSignedIn()) {
            Course course = courseViewModel.getCourse(note1.getCourseId());
            courseViewModel.addCourseToCloud(course, this, UserAccount.getInstance(this).getAccount().getDisplayName());
            noteViewModel.addNoteToCloud(note, this);
        }else {
            Toast.makeText(this, "Login First To Backup Notes", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMail(Note note) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT,note.getNoteTitle());
        intent.putExtra(Intent.EXTRA_TEXT, note.getNoteText());
        final Intent choose_app_to_send_mail = intent.createChooser(intent, getString(R.string.send_mail_intent_choose_title));
        intent.setType(getString(R.string.mail_intent_type));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(choose_app_to_send_mail);
        }
    }

    private Note getNoteUiUpdate() {
//        String courseTitle = (String) noteBinding.spinner.getSelectedItem();
//        int courseId = courseViewModel.getCourseIdByTitle(courseTitle);
//        if (courseId == -1) {
//            Log.e("NoteActivity", "onOptionsItemSelected: " + "Course Id is Invalid" , new InvalidObjectException("Course Object Invalid"));
//            return null;
//        }else {
//            note.setCourseId(courseId);
//        }
        if (!note.isNew()) {
            note.setId(note_id);
        }else {
            note.setNew(true);
        }
        return note;
    }

    private boolean preProcessBeforeAction() {
        boolean processedSuccessfully = true;
        if (TextUtils.isEmpty(noteBinding.editText2.getText())) {
            noteBinding.editText2.setError(getString(R.string.note_empty_error));
            processedSuccessfully = false;
        }
        return processedSuccessfully;
    }

    private Note isNoteDuplicate(Note note) {
        duplicatedNote = noteViewModel.getNoteWithTitleAndCourseId(note.getCourseId(),note.getNoteTitle());
        if(duplicatedNote != null && (duplicatedNote.isEmpty() || duplicatedNote.getId() != note.getId())) {
            note.setDuplicate(true);
        }else {
            note.setDuplicate(false);
        }
        return note;
    }


    private void setRemainder(final Note note1) {
        final GregorianCalendar calendar = new GregorianCalendar();
        TimePickerDialog.OnTimeSetListener onTimeSetListener = (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR,hourOfDay);
            calendar.set(Calendar.MINUTE,minute);
            setAlarm(calendar.getTimeInMillis(),note1);
        };
        TimePickerDialog timePickerDialog = new
                TimePickerDialog(this,onTimeSetListener,
                calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE),true);
        timePickerDialog.show();
    }

    private void setAlarm(long triggerTime, Note note1) {
        Intent intent = NoteRemainderReciever.getIntent(this,note1);
        PendingIntent pendingIntent = PendingIntent.
                getBroadcast(this,NOTE_REMAINDER_BROADCAST,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC,triggerTime,pendingIntent);
            Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
        }else {
            Log.d("NoteActivity", "notifyAlarmManger: Alarm didn't set");
        }
    }

    private void removeAssociatedReminder(Note note) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = NoteRemainderReciever.getIntent(this,note);
        PendingIntent pendingIntent = PendingIntent.
                getBroadcast(this,NOTE_REMAINDER_BROADCAST,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    @Override
    public void CourseUploadFailed(Exception e) {
        e.printStackTrace();
        Toast.makeText(this, "Note Upload Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void errorFetchingCourse() {
        return;
    }

    @Override
    public void addCourseToLocalDatabase(List<Course> courses) {
        return;
    }

    @Override
    public void courseUploadSucceeded(String referenceId) {
        Toast.makeText(this, "Course Uploaded With ID : " + referenceId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void noteUploadFailed(Exception e) {
        e.printStackTrace();
        Toast.makeText(this, "Note Upload Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void errorFetchingNotes() {
        return;
    }

    @Override
    public void addNoteToLocalDatabase(List<Note> notes, Map<Integer, Integer> courseIdMappings) {
        return;
    }

    @Override
    public void noteUploadSucceeded(String referenceId) {
        Toast.makeText(this, "Note Uploaded With ID : " + referenceId, Toast.LENGTH_SHORT).show();
    }
}
