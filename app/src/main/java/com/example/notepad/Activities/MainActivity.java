package com.example.notepad.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notepad.Adapter.CourseAdapter;
import com.example.notepad.Adapter.NoteAdapter;
import com.example.notepad.CallBackInteface.CourseCallbacks;
import com.example.notepad.CallBackInteface.NoteCallBacks;
import com.example.notepad.Course;
import com.example.notepad.Note;
import com.example.notepad.NoteWithCourse;
import com.example.notepad.R;
import com.example.notepad.UserAccount;
import com.example.notepad.ViewModel.CourseViewModel;
import com.example.notepad.ViewModel.NoteViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CourseCallbacks, NoteCallBacks {
    private static final String TAG = "MainActivity";
    private CourseViewModel mCourseViewModel;
    private CourseAdapter courseAdapter;
    private NoteViewModel noteViewModel;
    private NoteAdapter noteAdapter;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private int[] selectedNavItemId;
    RecyclerView recyclerView;
    UserAccount userAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if (selectedNavItemId[0] == R.id.notes_nav) {
                Intent intent = new Intent(MainActivity.this,NoteActivity.class);
                startActivity(intent);
            }else if(selectedNavItemId[0] == R.id.courses_nav) {
                Intent intent = new Intent(MainActivity.this,CourseActivity.class);
                startActivity(intent);
            }
        });

        mCourseViewModel = ViewModelProviders.of(this).get(CourseViewModel.class);
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);

        Observer<List<NoteWithCourse>> noteWithCourseObserver = new Observer<List<NoteWithCourse>>() {
            @Override
            public void onChanged(List<NoteWithCourse> noteWithCourseLiveData) {
                noteAdapter = new NoteAdapter(MainActivity.this,noteWithCourseLiveData);
                if (selectedNavItemId[0] == R.id.notes_nav) {
                    recyclerView.setAdapter(noteAdapter);
                }
            }
        };
        noteViewModel.getNoteWithCourse().observe(this,noteWithCourseObserver);
        Observer<List<Course>> courseObserve = new Observer<List<Course>>() {
            @Override
            public void onChanged(List<Course> listData) {
                courseAdapter = new CourseAdapter(MainActivity.this,listData);
                if (selectedNavItemId[0] == R.id.courses_nav) {
                    recyclerView.setAdapter(courseAdapter);
                }
            }
        };
        mCourseViewModel.getCourses().observe(this,courseObserve);
        setUpDrawerLayout();
        userAccount = UserAccount.getInstance(this);
        if (userAccount.ifPreviouslyLogin()) {
            Toast.makeText(this, "Logged in with " + userAccount.getAccount().getEmail(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,null,R.string.open_drawer , R.string.close_drawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        final NavigationView navigationView = findViewById(R.id.nav_view);

        selectedNavItemId = new int[]{R.id.notes_nav};
        NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (selectedNavItemId[0] != item.getItemId()) {
                    selectedNavItemId[0] = item.getItemId();
                    navigationView.setCheckedItem(selectedNavItemId[0]);
                    if (selectedNavItemId[0] == R.id.notes_nav) {
                        recyclerView.setAdapter(noteAdapter);
                    }else if(selectedNavItemId[0] == R.id.courses_nav) {
                        recyclerView.setAdapter(courseAdapter);
                    }
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        };
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == R.id.recover_notes_menu) {
            if (userAccount.isSignedIn()) {
                if (this instanceof CourseCallbacks) {
                    mCourseViewModel.getCoursesFromCloud(this, userAccount.getAccount().getDisplayName());
                }
            }else {
                Toast.makeText(this, "Login First to Fetch Notes From Cloud", Toast.LENGTH_SHORT).show();
            }
        } else if ( itemId == R.id.user_login_menu) {
            Intent intent = LoginUserActivity.getIntent(this);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void CourseUploadFailed(Exception e) {
        e.printStackTrace();
        Log.i(TAG, "uploadFailed: " + e.getMessage());
    }

    @Override
    public void errorFetchingCourse() {
        Log.i(TAG, "errorFetching: " + "Error fetching course");
    }

    @Override
    public void addCourseToLocalDatabase(List<Course> courses) {
        //old course Id and new course Id
        HashMap<Integer,Integer> map = new HashMap<>();
        for (Course course : courses) {
            if (course != null) {
                int oldId = course.getId();
                int newCourseId = mCourseViewModel.getCourseIdByTitle(course.getTitle());
                if (newCourseId == 0 || newCourseId == -1) {
                    mCourseViewModel.insert(course);
                    newCourseId = mCourseViewModel.getCourseIdByTitle(course.getTitle());
                }
                course.setId(newCourseId);
                map.put(oldId,newCourseId);
            }
        }
        noteViewModel.getNotesFromCloud(this,map);
        Log.i(TAG, "addToLocalDatabase: Courses Added");
    }

    @Override
    public void courseUploadSucceeded(String referenceId) {
        Toast.makeText(this, referenceId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void noteUploadFailed(Exception e) {
        e.printStackTrace();
        Log.i(TAG, "uploadFailed: " + e.getMessage());
    }

    @Override
    public void errorFetchingNotes() {
        Log.i(TAG, "errorFetching: " + "Error fetching Note");
    }

    @Override
    public void addNoteToLocalDatabase(List<Note> notes, Map<Integer,Integer> courseIdMappings) {
        //course callback should execute first : Done
        //need a map from course callback which maps the old course id to new course Id. : Done
        for (Note note : notes) {
            if (note != null) {
                //use course Id and get its mapped value and set the note course id to mapped value. : Done
                Integer courseNewId = courseIdMappings.get(note.getCourseId());
                if (courseNewId == null) {
                    Log.e(TAG, "addNoteToLocalDatabase: Wrong Course Id Mapping");
                    continue;
                }
                note.setCourseId(courseNewId);
                note.setId(0);
                //look if there is a note with mapped id and current note title. : Done
                //if no similar note avaliable then only add the note else don't operation futher on current note : Done
                Note note1 = noteViewModel.getNoteWithTitleAndCourseId(note.getCourseId(),note.getNoteTitle());
                if (note1 != null) {
                    Log.d(TAG, "addNoteToLocalDatabase() called with: Note with same course and title found");
                    return;
                }
                //insert the note : Done
                noteViewModel.insert(note);

            }
        }
    }

    @Override
    public void noteUploadSucceeded(String referenceId) {
        Toast.makeText(this, referenceId , Toast.LENGTH_SHORT).show();
    }
}