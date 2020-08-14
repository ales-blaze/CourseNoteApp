package com.example.notepad.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.notepad.Course;
import com.example.notepad.R;
import com.example.notepad.ViewModel.CourseViewModel;
import com.example.notepad.databinding.ActivityCourseBinding;
import com.google.gson.Gson;

public class CourseActivity extends AppCompatActivity {

    public static final String COURSE = "COURSE";
    private CourseViewModel courseViewModel;
    private ActivityCourseBinding courseBinding;
    private Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        courseViewModel = ViewModelProviders.of(this).get(CourseViewModel.class);
        courseBinding = DataBindingUtil.setContentView(this,R.layout.activity_course);

        course = new Course();
        if (getIntent() != null) {
             course.setId(getIntent().getIntExtra("COURSE_ID",0));
             if (course.getId() == 0) {
                 course.setTitle("");
                 course.setDescription("");
                 course.setNew(true);
             }else {
                 course = courseViewModel.getCourse(course.getId());
             }
        }else {
            return;
        }

        courseBinding.setCours(course);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.course_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.save_course) {
            if (course.isNew()) {
                courseViewModel.insert(course);
                Toast.makeText(this, "Inserted", Toast.LENGTH_SHORT).show();
            }else {
                courseViewModel.update(course);
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
            }
            finish();
        }else if(id == R.id.delete_course) {
            if (!course.isNew()) {
                courseViewModel.delete(course);
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Gson gson = new Gson();
        outState.putString(COURSE,gson.toJson(course));
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Gson gson = new Gson();
        String jsonString = savedInstanceState.getString(COURSE);
        course = gson.fromJson(jsonString,Course.class);
        courseBinding.setCours(course);
    }
}
